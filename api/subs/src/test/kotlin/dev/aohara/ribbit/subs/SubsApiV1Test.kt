package dev.aohara.ribbit.subs

import com.ribbit.core.AccessToken
import com.ribbit.core.User
import com.ribbit.core.UserId
import com.ribbit.core.createAuthorizer
import com.ribbit.subs.Sub
import com.ribbit.subs.api.SubDataDtoV1
import com.ribbit.subs.api.SubDtoV1
import com.ribbit.subs.api.subsApiV1
import com.ribbit.subs.subService
import com.ribbit.subs.subsTable
import com.ribbit.subs.subsTableName
import dev.aohara.ribbit.baseTestEnv
import dev.aohara.ribbit.clock
import dev.aohara.ribbit.valueOrThrow
import dev.aohara.ribbit.withAuthorizer
import dev.aohara.ribbit.withToken
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.kms.FakeKMS
import org.http4k.contract.contract
import org.http4k.contract.security.BearerAuthSecurity
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.RequestContexts
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ServerFilters
import org.http4k.kotest.shouldHaveStatus
import org.http4k.lens.RequestContextKey
import org.http4k.routing.reverseProxy
import org.http4k.testing.ApprovalTest
import org.http4k.testing.Approver
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ApprovalTest::class)
class SubsApiV1Test {
    private val kms = FakeKMS()
    private val dynamo = FakeDynamoDb(clock = clock)

    private val internet = reverseProxy(
        "kms" to kms,
        "dynamodb" to dynamo
    )

    private val env = baseTestEnv
        .withAuthorizer(kms.client())
        .with(
            subsTableName of dynamo.client()
                .subsTable(TableName.of("subs")).createTable()
                .valueOrThrow()
                .TableDescription.TableName!!
        )

    private val auth = createAuthorizer(env, clock, internet)
    private val service = subService(env, internet)

    private val requestContexts = RequestContexts()
    private val authLens = RequestContextKey.required<User>(requestContexts)
    private val bearerAuth = BearerAuthSecurity(
        authLens,
        lookup = { auth.authorize(AccessToken.of(it)) }
    )

    private val api = ServerFilters.InitialiseRequestContext(requestContexts).then(
        contract {
            routes += subsApiV1(service, authLens, bearerAuth)
        }
    )

    private fun createUser(id: String, name: String = "user$id"): Pair<User, AccessToken> {
        val user = User(UserId.of(id), name)
        return user to auth.issue(user)
    }

    @Test
    fun `get sub`(approval: Approver) {
        val (user, _) = createUser("1")
        val sub = service.create(user.id, "frogs")

        val response = Request(GET, "/subs/${sub.id}").let(api)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }

    @Test
    fun `get sub - missing`(approval: Approver) {
        val response = Request(GET, "/subs/missing").let(api)

        response shouldHaveStatus NOT_FOUND
    }

    @Test
    fun `create sub - unauthorized`() {
        Request(POST, "/subs")
            .with(SubDataDtoV1.lens of SubDataDtoV1.sample)
            .let(api)
            .shouldHaveStatus(UNAUTHORIZED)
    }

    @Test
    fun `create sub`() {
        val (user, token) = createUser("1")

        val response = Request(POST, "/subs")
            .with(SubDataDtoV1.lens of SubDataDtoV1.sample)
            .withToken(token)
            .let(api)

        response shouldHaveStatus OK

        val sub = SubDtoV1.lens(response)
        sub.name shouldBe SubDataDtoV1.sample.name

        service.getSub(sub.id) shouldBeSuccess Sub(
            id = sub.id,
            name = sub.name,
            owner = user.id
        )
    }
}