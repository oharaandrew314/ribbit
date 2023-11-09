package com.ribbit

import com.github.ksuid.Ksuid
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.proc.SingleKeyJWSKeySelector
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.ribbit.posts.Post
import com.ribbit.posts.PostId
import com.ribbit.posts.postsAuthorIndex
import com.ribbit.posts.postsSubIndex
import com.ribbit.posts.postsTable
import com.ribbit.subs.Sub
import com.ribbit.subs.SubId
import com.ribbit.subs.subsTable
import com.ribbit.users.EmailHash
import com.ribbit.users.User
import com.ribbit.users.UserData
import com.ribbit.users.Username
import com.ribbit.users.userNamesIndex
import com.ribbit.users.usersTable
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.onFailure
import io.andrewohara.utils.jdk.toClock
import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_ACCESS_KEY_ID
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.AWS_SECRET_ACCESS_KEY
import org.http4k.connect.amazon.core.model.AccessKeyId
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.SecretAccessKey
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.routing.reverseProxy
import java.security.KeyPairGenerator
import java.time.Duration
import java.time.Instant

class TestDriver: ChaoticHttpHandler() {
    private val dynamo = FakeDynamoDb()

    val clock = Instant.parse("2023-07-06T12:00:00Z").toClock()
    val time get() = clock.instant()

    private val jwsKeyPair = KeyPairGenerator.getInstance("RSA")
        .apply { initialize(2048) }
        .generateKeyPair()

    private val internet = reverseProxy(
        "dynamodb" to dynamo
    )

    private val env = Environment.defaults(
        AWS_REGION of Region.CA_CENTRAL_1,
        AWS_ACCESS_KEY_ID of AccessKeyId.of("fake-id"),
        AWS_SECRET_ACCESS_KEY of SecretAccessKey.of("fake-value"),
        Settings.jwtIssuer of Uri.of("http://ribbit-auth"),
        Settings.jwtAudiences of listOf("ribbit-test"),
        Settings.corsOrigins of listOf("http://localhost"),
        Settings.postsTableName of dynamo.client()
            .postsTable(TableName.of("posts"))
            .createTable(postsSubIndex, postsAuthorIndex)
            .valueOrThrow()
            .TableDescription.TableName!!,
        Settings.subsTableName of dynamo.client()
            .subsTable(TableName.of("subs")).createTable()
            .valueOrThrow()
            .TableDescription.TableName!!,
        Settings.usersTableName of dynamo.client()
            .usersTable(TableName.of("users"))
            .createTable(userNamesIndex)
            .valueOrThrow()
            .TableDescription.TableName!!,
        Settings.randomSeed of 1337
    )

    val service = ribbitService(
        env, clock, internet,
        keySelector = SingleKeyJWSKeySelector(JWSAlgorithm.RS256, jwsKeyPair.public)
    )

    override val app = service.toApi(env)

    fun createToken(name: String): String {
        val header = JWSHeader.Builder(JWSAlgorithm.RS256).build()
        val claims = JWTClaimsSet.Builder()
            .audience(env[Settings.jwtAudiences])
            .issuer(env[Settings.jwtIssuer].toString())
            .claim("email", "$name@ribbit.com")
            .build()

        return SignedJWT(header, claims)
            .apply { sign(RSASSASigner(jwsKeyPair.private)) }
            .serialize()
    }
}

fun TestDriver.createToken(user: User) = createToken(user.name.value)

fun TestDriver.createUser(name: String): User {
    return service.users.create(
        emailHash = EmailHash.fromEmail("$name@ribbit.com"),
        data = UserData(name = Username.of(name))
    ).valueOrThrow()
}

fun TestDriver.createPost(
    sub: Sub,
    author: User,
    time: Instant = clock + 1.seconds,
    id: Ksuid = service.nextId.newKsuid(time),
    title: String = "post$id",
    content: String = "Stuff about $title"
) = Post(
    id = PostId.of(id),
    title = title,
    content = content,
    updated = null,
    authorName = author.name,
    subId = sub.id,
).also {
    service.posts.repo += it
}

fun TestDriver.createSub(owner: User, id: String, name: String = "sub$id") = Sub(
    id = SubId.of(id),
    name = name,
    owner = owner.name
).also { service.subs.subs += it }

fun Request.withToken(token: String) = header("Authorization", "Bearer $token")

fun <R: Any, E: Any> Result4k<R, E>.valueOrThrow(): R = onFailure { error(it) }

val Int.seconds get(): Duration = Duration.ofSeconds(toLong())
val Int.hours get(): Duration = Duration.ofHours(toLong())