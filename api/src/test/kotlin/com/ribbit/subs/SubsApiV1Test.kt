package com.ribbit.subs

import com.ribbit.TestDriver
import com.ribbit.createSub
import com.ribbit.subs.api.SubDataDtoV1
import com.ribbit.subs.api.SubDtoV1
import com.ribbit.withToken
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.CONFLICT
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
class SubsApiV1Test {

    private val driver = TestDriver()

    @Test
    fun `get sub`(approval: Approver) {
        val (user, _) = driver.createUser("1")
        val sub = driver.createSub(user, "frogs")

        val response = Request(GET, "/subs/${sub.id}").let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }

    @Test
    fun `get sub - missing`(approval: Approver) {
        val response = Request(GET, "/subs/missing").let(driver)

        response shouldHaveStatus NOT_FOUND
        approval.assertApproved(response)
    }

    @Test
    fun `create sub - unauthorized`() {
        Request(POST, "/subs")
            .with(SubDataDtoV1.lens of SubDataDtoV1.sample)
            .let(driver)
            .shouldHaveStatus(UNAUTHORIZED)
    }

    @Test
    fun `create sub`(approval: Approver) {
        val (user, token) = driver.createUser("1")

        val response = Request(POST, "/subs")
            .with(SubDataDtoV1.lens of SubDataDtoV1.sample)
            .withToken(token)
            .let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)

        val sub = SubDtoV1.lens(response)
        sub.name shouldBe SubDataDtoV1.sample.name

        driver.service.subs.getSub(sub.id) shouldBeSuccess Sub(
            id = sub.id,
            name = sub.name,
            owner = user.id
        )
    }

    @Test
    fun `create sub - duplicate`(approval: Approver) {
        val (user, token) = driver.createUser("1")
        driver.createSub(user, SubDataDtoV1.sample.id.value)

        val response = Request(POST, "/subs")
            .with(SubDataDtoV1.lens of SubDataDtoV1.sample)
            .withToken(token)
            .let(driver)

        response shouldHaveStatus CONFLICT
        approval.assertApproved(response)
    }

    // TODO support pagination
    @Test
    fun `list subs`(approval: Approver) {
        val (user, _) = driver.createUser("1")

        driver.createSub(user, "111")
        driver.createSub(user, "222")

        val response = Request(GET, "/subs").let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }
}