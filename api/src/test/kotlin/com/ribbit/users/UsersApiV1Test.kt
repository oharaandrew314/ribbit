package com.ribbit.users

import com.ribbit.TestDriver
import com.ribbit.users.api.UserDataDtoV1
import com.ribbit.withToken
import io.kotest.matchers.shouldBe
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.with
import org.http4k.kotest.shouldHaveStatus
import org.http4k.testing.Approver
import org.http4k.testing.JsonApprovalTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(JsonApprovalTest::class)
class UsersApiV1Test {

    private val driver = TestDriver()

    @Test
    fun `get user - self`(approve: Approver) {
        val user = driver.createUser("1")

        val response = Request(GET, "/users/${user.name}")
            .withToken(driver.createToken(user))
            .let(driver)

        response shouldHaveStatus OK
        approve.assertApproved(response)
    }

    @Test
    fun `get user - unauthenticated`(approve: Approver) {
        val user = driver.createUser("1")

        val response = Request(GET, "/users/${user.name}")
            .let(driver)

        response shouldHaveStatus OK
        approve.assertApproved(response)
    }

    @Test
    fun `get missing user`(approval: Approver) {
        val token = driver.createToken("2")

        val response = Request(GET, "/users/missing")
            .withToken(token)
            .let(driver)

        response shouldHaveStatus NOT_FOUND
        approval.assertApproved(response)
    }

    @Test
    fun `create profile - unauthenticated`() {
        val response = Request(POST, "/users")
            .with(UserDataDtoV1.lens of UserDataDtoV1.sample)
            .let(driver)

        response shouldHaveStatus UNAUTHORIZED
    }

    @Test
    fun `create profile - success`(approval: Approver) {
        val token = driver.createToken("1")

        val response = Request(POST, "/users")
            .with(UserDataDtoV1.lens of UserDataDtoV1(Username.of("foo")))
            .withToken(token)
            .let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)

        driver.service.users.repo[Username.of("foo")] shouldBe User(
            emailHash = EmailHash.of("qWak7RfmrlLD7Daz6bsozkJKcw"),
            name = Username.of("foo")
        )
    }

    @Test
    fun `get your profile - unauthorized`() {
        val response = Request(GET, "/users").let(driver)

        response shouldHaveStatus UNAUTHORIZED
    }

    @Test
    fun `get your profile - not created`(approval: Approver) {
        val token = driver.createToken("1")

        val response = Request(GET, "/users")
            .withToken(token)
            .let(driver)

        response shouldHaveStatus NOT_FOUND
        approval.assertApproved(response)
    }

    @Test
    fun `get your profile - success`(approval: Approver) {
        val user = driver.createUser("1")
        val token = driver.createToken(user.name.value)

        val response = Request(GET, "/users")
            .withToken(token)
            .let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }
}