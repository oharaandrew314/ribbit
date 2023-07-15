package com.ribbit.users

import com.ribbit.TestDriver
import com.ribbit.users.api.UserDataDtoV1
import com.ribbit.withToken
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
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
        val (user, token) = driver.createUser("1")

        val response = Request(GET, "/users/${user.id}")
            .withToken(token)
            .let(driver)

        response shouldHaveStatus OK
        approve.assertApproved(response)
    }

    @Test
    fun `get user - unauthenticated`(approve: Approver) {
        val (user, _) = driver.createUser("1")

        val response = Request(GET, "/users/${user.id}")
            .let(driver)

        response shouldHaveStatus OK
        approve.assertApproved(response)
    }

    @Test
    fun `get missing user`(approval: Approver) {
        val (_, token) = driver.createUser("2")

        val response = Request(GET, "/users/missing")
            .withToken(token)
            .let(driver)

        response shouldHaveStatus NOT_FOUND
        approval.assertApproved(response)
    }

    @Test
    fun `update profile - unauthenticated`() {
        val response = Request(PUT, "/users")
            .with(UserDataDtoV1.lens of UserDataDtoV1.sample)
            .let(driver)

        response shouldHaveStatus UNAUTHORIZED
    }

    @Test
    fun `update profile - success`(approval: Approver) {
        val (user, token) = driver.createUser("3")

        val response = Request(PUT, "/users")
            .with(UserDataDtoV1.lens of UserDataDtoV1("foo"))
            .withToken(token)
            .let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)

        driver.service.users.repo[user.id]
            .shouldNotBeNull()
            .name shouldBe "foo"
    }
}