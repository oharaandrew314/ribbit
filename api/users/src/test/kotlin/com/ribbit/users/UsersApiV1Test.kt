package com.ribbit.users

import com.ribbit.core.User
import com.ribbit.core.UserId
import com.ribbit.core.issuerKey
import com.ribbit.core.tokensKeyIdKey
import com.ribbit.users.api.usersApiV1
import dev.aohara.ribbit.baseTestEnv
import dev.aohara.ribbit.valueOrThrow
import dev.aohara.ribbit.withToken
import io.andrewohara.utils.jdk.toClock
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.kms.FakeKMS
import org.http4k.connect.amazon.kms.createKey
import org.http4k.connect.amazon.kms.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.kms.model.KeyUsage
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.contract.contract
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.kotest.shouldHaveStatus
import org.http4k.routing.reverseProxy
import org.http4k.testing.Approver
import org.http4k.testing.JsonApprovalTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant

@ExtendWith(JsonApprovalTest::class)
class UsersApiV1Test {
    private val clock = Instant.parse("2023-07-06T12:00:00Z").toClock()

    private val kms = FakeKMS()
    private val table = Storage.InMemory<DynamoTable>()
    private val dynamo = FakeDynamoDb(table, clock = clock)

    private val internet = reverseProxy(
        "kms" to kms,
        "dynamodb" to dynamo
    )

    private val env = baseTestEnv.with(
        usersTableNameKey of dynamo.client()
            .tableMapper<User, UserId, Unit>(TableName.of("users"), userIdAttr, null)
            .createTable()
            .valueOrThrow()
            .TableDescription.TableName!!,
        googleAudienceKey of "google-aud",
        tokensKeyIdKey of kms.client()
            .createKey(CustomerMasterKeySpec.ECC_NIST_P521, KeyUsage.SIGN_VERIFY)
            .valueOrThrow()
            .KeyMetadata.KeyId,
        issuerKey of "ribbit-test"
    )

    private val service = userService(env, clock, aws = internet)

    private val api = contract {
        routes += usersApiV1(service)
    }

    @Test
    fun `get self`(approve: Approver) {
        val (user, token) = service.create(id = "1")

        val response = Request(GET, "/users/${user.id}")
            .withToken(token)
            .let(api)

        response shouldHaveStatus OK
        approve.assertApproved(response)
    }

    @Test
    fun `get missing user`() {
        val (_, token) = service.create(id = "2")

        val response = Request(GET, "/users/missing")
            .withToken(token)
            .let(api)

        response shouldHaveStatus NOT_FOUND
    }

    @Test
    fun `get other user`(approve: Approver) {
        val (_, token) = service.create(id = "3")
        val (otherUser, _) = service.create(id = "4")

        val response = Request(GET, "/users/${otherUser.id}")
            .withToken(token)
            .let(api)

        response shouldHaveStatus OK
        approve.assertApproved(response)
    }
}