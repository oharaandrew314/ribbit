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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

@ExtendWith(JsonApprovalTest::class)
class PostsApiV1Test {

    private val driver = TestDriver()

    private val user1Result = driver.createUser("1")
    private val user1 = user1Result.first
    private val user1Token = user1Result.second

    private val user2Result = driver.createUser("2")
    private val user2 = user2Result.first
    private val user2Token = user2Result.second

    private val sub1 = driver.createSub(user1, "111")
    private val post1 = driver.createPost(sub1, user1)
    private val post2 = driver.createPost(sub1, user2)

    private val sub2 = driver.createSub(user2, "222")
    private val post3 = driver.createPost(sub2, user1)

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
    fun `list posts in sub - missing`() {
        Request(GET, "/subs/missing/posts")
            .let(driver)
            .shouldHaveStatus(NOT_FOUND)
    }

    @Test
    fun `list posts in sub - by latest`(approval: Approver) {
        val time = driver.clock.instant()

        val sub3 = driver.createSub(user1, "333")
        driver.createPost(sub3, user1, driver.ksuidGen.newKsuid(time), title = "post 1")
        driver.createPost(sub3, user1, driver.ksuidGen.newKsuid(time.plusSeconds(1)), title = "post 2")
        driver.createPost(sub3, user1, driver.ksuidGen.newKsuid(time.plusSeconds(2)), title = "post 3")

        val response = Request(GET, "/subs/${sub3.id}/posts").let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }

    @Test
    fun `list posts from author`(approval: Approver) {
        val response = Request(GET, "/users/${user1.id}/posts").let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)
    }

    @Test
    fun `list posts from author - missing`() {
        Request(GET, "/users/missing/posts")
            .let(driver)
            .shouldHaveStatus(NOT_FOUND)
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
            .withToken(user1Token)
            .let(driver)

        response shouldHaveStatus OK
        val post = PostDtoV1.lens(response)

        post.created shouldBe driver.clock.instant()
        post.updated.shouldBeNull()
        post.authorId shouldBe user1.id
        post.title shouldBe "frogs are cool"
        post.content shouldBe "very cool"

        driver.service.posts.posts[sub1.id].toList().shouldContainExactlyInAnyOrder(
            post1, post2,
            Post(
                id = post.id,
                authorId = user1.id,
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
            .withToken(user1Token)
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
        val postId = driver.ksuidGen.newKsuid(driver.clock.instant())
        val response = Request(PUT, "/posts/$postId")
            .with(PostDataDtoV1.lens of data)
            .withToken(user1Token)
            .let(driver)

        response shouldHaveStatus NOT_FOUND
        approval.assertApproved(response)
    }

    @Test
    fun `edit post`(approval: Approver) {
        driver.clock += Duration.ofHours(1)

        val response = Request(PUT, "/posts/${post2.id}")
            .with(PostDataDtoV1.lens of data)
            .withToken(user2Token)
            .let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)

        driver.service.posts.posts[post2.id] shouldBe post2.copy(
            title = "frogs are cool",
            content = "very cool",
            updated = driver.clock.instant()
        )
    }

    @Test
    fun `edit post - not author`(approval: Approver) {
        val response = Request(PUT, "/posts/${post2.id}")
            .with(PostDataDtoV1.lens of data)
            .withToken(user1Token)
            .let(driver)

        response shouldHaveStatus FORBIDDEN
        approval.assertApproved(response)
    }

    @Test
    fun `delete post - not found`(approval: Approver) {
        val postId = driver.ksuidGen.newKsuid(driver.clock.instant())
        val response = Request(DELETE, "/posts/$postId")
            .withToken(user1Token)
            .let(driver)

        response shouldHaveStatus NOT_FOUND
        approval.assertApproved(response)
    }

    @Test
    fun `delete post`(approval: Approver) {
        val response = Request(DELETE, "/posts/${post3.id}")
            .withToken(user1Token)
            .let(driver)

        response shouldHaveStatus OK
        approval.assertApproved(response)

        driver.service.posts.posts[post3.id].shouldBeNull()
    }

    @Test
    fun `delete post - not author`(approval: Approver) {
        val response = Request(DELETE, "/posts/${post1.id}")
            .withToken(user2Token)
            .let(driver)

        response shouldHaveStatus FORBIDDEN
        approval.assertApproved(response)
    }
}