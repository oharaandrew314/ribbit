package com.ribbit.posts

import com.ribbit.TestDriver
import com.ribbit.createSub
import com.ribbit.posts.api.PostDataDtoV1
import com.ribbit.posts.api.PostDtoV1
import com.ribbit.withToken
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.with
import org.http4k.kotest.shouldHaveStatus
import org.http4k.testing.Approver
import org.http4k.testing.JsonApprovalTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

@ExtendWith(JsonApprovalTest::class)
class PostsApiV1Test {

    private val driver = TestDriver()

    private val user1 = driver.createUser("1")
    private val user2 = driver.createUser("2")

    private val sub1 = driver.createSub(user1, "111")
    private val post1 = driver.createPost(sub1, user1)
    private val post2 = driver.createPost(sub1, user2)

    private val sub2 = driver.createSub(user2, "222")
    private val post3 = driver.createPost(sub2, user1)

    private val sub3 = driver.createSub(user1, "333").also { sub3 ->
        driver.createPost(sub3, user1, driver.clock.instant(), title = "post 4")
        driver.createPost(sub3, user1, driver.clock.instant().plusSeconds(1), title = "post 5")
        driver.createPost(sub3, user1, driver.clock.instant().plusSeconds(2), title = "post 6")
    }

    private val data = PostDataDtoV1(
        title = "frogs are cool",
        content = "very cool"
    )

    @Test
    fun `list posts in sub`(approval: Approver) {
        val response = Request(GET, "/subs/${sub1.id}/posts").let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }

    @Test
    fun `list posts in sub - missing`(approval: Approver) {
        val response = Request(GET, "/subs/missing/posts").let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }

    @Test
    fun `list posts in sub - by latest`(approval: Approver) {
        val response = Request(GET, "/subs/${sub3.id}/posts").let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }

    @Test
    @Disabled("pagination not yet supported")
    fun `list posts in sub - page 1 of 2`(approval: Approver) {
        val response = Request(GET, "/subs/${sub3.id}/posts").let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }

    @Test
    fun `list posts from author`(approval: Approver) {
        val response = Request(GET, "/users/${user1.name}/posts").let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }

    @Test
    fun `list posts from author - missing`(approval: Approver) {
        val response = Request(GET, "/users/missing/posts").let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }

    @Test
    fun `get post`(approval: Approver) {
        val response = Request(GET, "/posts/${post1.id}").let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }

    @Test
    fun `get post - missing`() {
        Request(GET, "/posts/missing")
            .let(driver)
            .shouldHaveStatus(NOT_FOUND)
    }

    @Test
    fun `create post`() {
        val response = Request(POST, "/subs/${sub1.id}/posts")
            .with(PostDataDtoV1.lens of data)
            .withToken(driver.createToken(user1))
            .let(driver)

        response shouldHaveStatus OK
        val post = PostDtoV1.lens(response)

        post.created shouldBe driver.clock.instant()
        post.updated.shouldBeNull()
        post.authorName shouldBe user1.name
        post.title shouldBe "frogs are cool"
        post.content shouldBe "very cool"

        driver.service.posts.repo[sub1.id].items().toList().shouldContainExactlyInAnyOrder(
            post1, post2,
            Post(
                id = post.id,
                authorName = user1.name,
                title = "frogs are cool",
                content = "very cool",
                updated = null,
                subId = sub1.id
            )
        )
    }

    @Test
    fun `create post - missing sub`() {
        Request(POST, "/subs/missing/posts")
            .with(PostDataDtoV1.lens of data)
            .withToken(driver.createToken(user1))
            .let(driver)
            .shouldHaveStatus(NOT_FOUND)
    }

    @Test
    fun `create post - unauthorized`() {
        val response = Request(POST, "/subs/${sub1.id}/posts")
            .with(PostDataDtoV1.lens of data)
            .let(driver)

        response shouldHaveStatus UNAUTHORIZED
    }

    @Test
    fun `edit post - not found`(approval: Approver) {
        val response = Request(PUT, "/posts/2SaFUudxpuvtl33E6gnd80YyGW4")
            .with(PostDataDtoV1.lens of data)
            .withToken(driver.createToken(user1))
            .let(driver)

        response shouldHaveStatus NOT_FOUND
        approval.assertApproved(response)
    }

    @Test
    fun `edit post`(approval: Approver) {
        driver.clock += Duration.ofHours(1)

        val response = Request(PUT, "/posts/${post2.id}")
            .with(PostDataDtoV1.lens of data)
            .withToken(driver.createToken(user2))
            .let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)

        driver.service.posts.repo[post2.id] shouldBe post2.copy(
            title = "frogs are cool",
            content = "very cool",
            updated = driver.clock.instant()
        )
    }

    @Test
    fun `edit post - not author`(approval: Approver) {
        val response = Request(PUT, "/posts/${post2.id}")
            .with(PostDataDtoV1.lens of data)
            .withToken(driver.createToken(user1))
            .let(driver)

        response shouldHaveStatus FORBIDDEN
        approval.assertApproved(response)
    }

    @Test
    fun `delete post - not found`(approval: Approver) {
        val response = Request(DELETE, "/posts/2SaFUudxpuvtl33E6gnd80YyGW4")
            .withToken(driver.createToken(user1))
            .let(driver)

        response shouldHaveStatus NOT_FOUND
        approval.assertApproved(response)
    }

    @Test
    fun `delete post`(approval: Approver) {
        val response = Request(DELETE, "/posts/${post3.id}")
            .withToken(driver.createToken(user1))
            .let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)

        driver.service.posts.repo[post3.id].shouldBeNull()
    }

    @Test
    fun `delete post - not author`(approval: Approver) {
        val response = Request(DELETE, "/posts/${post1.id}")
            .withToken(driver.createToken(user2))
            .let(driver)

        response shouldHaveStatus FORBIDDEN
        approval.assertApproved(response)
    }
}