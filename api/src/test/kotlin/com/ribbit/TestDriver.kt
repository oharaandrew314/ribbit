package com.ribbit

import com.ribbit.auth.AccessToken
import com.ribbit.posts.Post
import com.ribbit.posts.PostId
import com.ribbit.posts.postsTable
import com.ribbit.subs.Sub
import com.ribbit.subs.SubData
import com.ribbit.subs.SubId
import com.ribbit.subs.subsTable
import com.ribbit.users.User
import com.ribbit.users.UserId
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
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.kms.FakeKMS
import org.http4k.connect.amazon.kms.createKey
import org.http4k.connect.amazon.kms.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.kms.model.KeyUsage
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.routing.reverseProxy
import java.time.Instant

class TestDriver: HttpHandler {
    private val kms = FakeKMS()
    private val dynamo = FakeDynamoDb()

    val clock = Instant.parse("2023-07-06T12:00:00Z").toClock()

    private val internet = reverseProxy(
        "kms" to kms,
        "dynamo" to dynamo
    )

    private val env = Environment.defaults(
        AWS_REGION of Region.CA_CENTRAL_1,
        AWS_ACCESS_KEY_ID of AccessKeyId.of("fake-id"),
        AWS_SECRET_ACCESS_KEY of SecretAccessKey.of("fake-value"),
        Settings.jwtIssuer of "ribbit-test",
        Settings.googleAudience of "google-ribbit",
        Settings.corsOrigins of listOf("http://localhost"),
        Settings.tokensKeyId of kms.client()
            .createKey(CustomerMasterKeySpec.RSA_2048, KeyUsage.SIGN_VERIFY)
            .valueOrThrow()
            .KeyMetadata.KeyId,
        Settings.postsTableName of dynamo.client()
            .postsTable(TableName.of("posts")).createTable()
            .valueOrThrow()
            .TableDescription.TableName!!,
        Settings.subsTableName of dynamo.client()
            .subsTable(TableName.of("subs")).createTable()
            .valueOrThrow()
            .TableDescription.TableName!!,
        Settings.usersTableName of dynamo.client()
            .usersTable(TableName.of("users")).createTable()
            .valueOrThrow()
            .TableDescription.TableName!!
    )

    val service = ribbitService(env, clock, internet)

    override fun invoke(request: Request) = service.toApi(env)(request)
}

fun TestDriver.createUser(id: String, name: String = "user$id"): User {
    return service.users.createUser(UserId.of(id), name)
}

fun TestDriver.createSub(owner: User, id: String, name: String = "sub$id"): Sub {
    val data = SubData(id = SubId.of(id), name = name)
    return service.subs.createSub(owner.id, data).valueOrThrow()
}

fun TestDriver.issueToken(user: User): AccessToken {
    return service.issuer.issue(user)
}

fun TestDriver.createPost(sub: Sub, author: User, id: String, title: String = "post$id", content: String = "Stuff about $title"): Post {
    val post = Post(
        id = PostId.of(id),
        title = title,
        content = content,
        created = clock.instant(),
        updated = null,
        authorId = author.id,
        subId = sub.id,
    )
    service.posts.posts += post
    return post
}

fun Request.withToken(token: AccessToken) = header("Authorization", "Bearer ${token.value}")

fun <R: Any, E: Any> Result4k<R, E>.valueOrThrow(): R = onFailure { error(it) }