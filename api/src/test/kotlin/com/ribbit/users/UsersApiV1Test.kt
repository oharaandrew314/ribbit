package com.ribbit.users

import com.ribbit.TestDriver
import com.ribbit.createUser
import com.ribbit.issueToken
import com.ribbit.withToken
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
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

        val response = Request(GET, "/users/${user.id}")
            .withToken(driver.issueToken(user))
            .let(driver)

        response shouldHaveStatus OK
        approve.assertApproved(response)
    }

    @Test
    fun `get user - unauthenticated`(approve: Approver) {
        val user = driver.createUser("1")

        val response = Request(GET, "/users/${user.id}")
            .let(driver)

        response shouldHaveStatus OK
        approve.assertApproved(response)
    }

    @Test
    fun `get missing user`() {
        val user = driver.createUser("2")

        val response = Request(GET, "/users/missing")
            .withToken(driver.issueToken(user))
            .let(driver)

        response shouldHaveStatus NOT_FOUND
    }
}