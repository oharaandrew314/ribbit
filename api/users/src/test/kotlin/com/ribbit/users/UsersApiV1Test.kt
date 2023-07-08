package com.ribbit.users

import com.ribbit.users.api.usersApiV1
import dev.aohara.ribbit.baseTestEnv
import dev.aohara.ribbit.clock
import dev.aohara.ribbit.valueOrThrow
import dev.aohara.ribbit.withAuthorizer
import dev.aohara.ribbit.withToken
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.kms.FakeKMS
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

@ExtendWith(JsonApprovalTest::class)
class UsersApiV1Test {
    private val kms = FakeKMS()
    private val dynamo = FakeDynamoDb(clock = clock)

    private val internet = reverseProxy(
        "kms" to kms,
        "dynamodb" to dynamo
    )

    private val env = baseTestEnv
        .withAuthorizer(kms.client())
        .with(
            usersTableNameKey of dynamo.client()
                .usersTable(TableName.of("users"))
                .createTable()
                .valueOrThrow()
                .TableDescription.TableName!!,
            googleAudienceKey of "google-aud"
        )

    private val service = userService(env, clock, internet)

    private val api = contract {
        routes += usersApiV1(service)
    }

    @Test
    fun `get user - self`(approve: Approver) {
        val (user, token) = service.create(id = "1")

        val response = Request(GET, "/users/${user.id}")
            .withToken(token)
            .let(api)

        response shouldHaveStatus OK
        approve.assertApproved(response)
    }

    @Test
    fun `get user - unauthenticated`(approve: Approver) {
        val (user, _) = service.create(id = "1")

        val response = Request(GET, "/users/${user.id}")
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
}