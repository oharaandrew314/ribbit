package com.ribbit

import com.github.ksuid.Ksuid
import com.github.ksuid.KsuidGenerator
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.proc.SingleKeyJWSKeySelector
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.ribbit.posts.Post
import com.ribbit.posts.PostId
import com.ribbit.posts.createWithIndices
import com.ribbit.posts.postsTable
import com.ribbit.subs.Sub
import com.ribbit.subs.SubData
import com.ribbit.subs.SubId
import com.ribbit.subs.subsTable
import com.ribbit.users.User
import com.ribbit.users.usersTable
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.onFailure
import io.andrewohara.utils.jdk.toClock
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_ACCESS_KEY_ID
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.AWS_SECRET_ACCESS_KEY
import org.http4k.connect.amazon.core.model.AccessKeyId
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.SecretAccessKey
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.routing.reverseProxy
import java.security.KeyPairGenerator
import java.time.Instant
import java.util.Random

class TestDriver: HttpHandler {
    private val tables = Storage.InMemory<DynamoTable>()
    private val dynamo = FakeDynamoDb(tables)

    val clock = Instant.parse("2023-07-06T12:00:00Z").toClock()
    private val ksuidGen = KsuidGenerator(Random(42))
    private val keyPair = KeyPairGenerator.getInstance("RSA")
        .apply { initialize(2048) }
        .generateKeyPair()

    private val internet = reverseProxy(
        "dynamo" to dynamo
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
            .createWithIndices()
            .valueOrThrow()
            .TableDescription.TableName!!,
        Settings.subsTableName of dynamo.client()
            .subsTable(TableName.of("subs")).createTable()
            .valueOrThrow()
            .TableDescription.TableName!!,
        Settings.usersTableName of dynamo.client()
            .usersTable(TableName.of("users")).createTable()
            .valueOrThrow()
            .TableDescription.TableName!!,
        Settings.randomSeed of 1337
    )

    val service = ribbitService(
        env, clock, internet,
        keySelector = SingleKeyJWSKeySelector(JWSAlgorithm.RS256, keyPair.public)
    )

    override fun invoke(request: Request) = service.toApi(env)(request)

    fun createUser(name: String, email: String = "$name@ribbit.test"): Pair<User, String> {
        val header = JWSHeader.Builder(JWSAlgorithm.RS256).build()
        val claims = JWTClaimsSet.Builder()
            .audience(env[Settings.jwtAudiences])
            .issuer(env[Settings.jwtIssuer].toString())
            .claim("email", email)
            .build()

        val token = SignedJWT(header, claims)
            .apply { sign(RSASSASigner(keyPair.private)) }
            .serialize()

        val user = User(
            id = service.authorizer(token)!!,
            name = name
        )
        service.users.repo += user

        return user to token
    }

    fun createPost(
        sub: Sub,
        author: User,
        time: Instant = clock.instant(),
        id: Ksuid = ksuidGen.newKsuid(time),
        title: String = "post$id",
        content: String = "Stuff about $title"
    ): Post {
        println("create post $id")
        val post = Post(
            id = PostId.of(id),
            title = title,
            content = content,
            updated = null,
            authorId = author.id,
            subId = sub.id,
        )
        service.posts.repo += post
        return post
    }
}

fun TestDriver.createSub(owner: User, id: String, name: String = "sub$id"): Sub {
    val data = SubData(id = SubId.of(id), name = name)
    return service.subs.createSub(owner.id, data).valueOrThrow()
}

fun Request.withToken(token: String) = header("Authorization", "Bearer $token")

fun <R: Any, E: Any> Result4k<R, E>.valueOrThrow(): R = onFailure { error(it) }