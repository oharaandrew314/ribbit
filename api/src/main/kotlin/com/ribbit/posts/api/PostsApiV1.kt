package com.ribbit.posts.api

import com.ribbit.posts.Post
import com.ribbit.posts.PostId
import com.ribbit.posts.PostService
import com.ribbit.posts.lens
import com.ribbit.subs.SubId
import com.ribbit.toResponse
import com.ribbit.users.User
import com.ribbit.users.UserId
import dev.forkhandles.result4k.get
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import org.http4k.contract.ContractRoute
import org.http4k.contract.Tag
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.contract.security.Security
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.RequestContextLens

fun postsApiV1(service: PostService, auth: RequestContextLens<User>, bearerAuth: Security): List<ContractRoute> {
    val tag = Tag("Posts")

    val create = "/subs" / SubId.lens / "posts" meta {
        operationId = "createPost"
        summary = "Create Post"
        tags += tag
        security = bearerAuth

        receiving(PostDataDtoV1.lens to PostDataDtoV1.sample)
        returning(OK, PostDtoV1.lens to PostDtoV1.sample)
    } bindContract POST to { subId, _ ->
        { request ->
            service.createPost(auth(request).id, subId, PostDataDtoV1.lens(request).toModel())
                .map { Response(OK).with(PostDtoV1.lens of it.toDtoV1()) }
                .mapFailure { it.toResponse() }
                .get()
        }
    }

    val list = "/subs" / SubId.lens / "posts" meta {
        operationId = "listSUbPosts"
        summary = "List Posts for Sub"
        tags += tag

        returning(OK, PostDtoV1.manyLens to arrayOf(PostDtoV1.sample))
        returning(NOT_FOUND to "sub not found")
    } bindContract GET to { subId, _ ->
        {
            service.getPosts(subId)
                .map { Response(OK).with(PostDtoV1.manyLens of it.map(Post::toDtoV1).toTypedArray()) }
                .mapFailure { it.toResponse() }
                .get()
        }
    }

    val listByAuthor = "/users" / UserId.lens / "posts" meta {
        operationId = "listPostsByAuthor"
        summary = "List Posts for Author"
        tags += tag

        returning(OK, PostDtoV1.manyLens to arrayOf(PostDtoV1.sample))
    } bindContract GET to { userId, _ ->
        {
            service.getPosts(userId)
                .map { Response(OK).with(PostDtoV1.manyLens of it.map(Post::toDtoV1).toTypedArray()) }
                .mapFailure { it.toResponse() }
                .get()
        }
    }

    val edit = "/posts" / PostId.lens meta {
        operationId = "editPost"
        summary = "Edit Post"
        tags += tag
        security = bearerAuth

        returning(OK, PostDtoV1.lens to PostDtoV1.sample)
        returning(NOT_FOUND to "post not found")
    } bindContract PUT to { postId ->
        { request ->
            service.editPost(auth(request).id, postId, PostDataDtoV1.lens(request).toModel())
                .map { Response(OK).with(PostDtoV1.lens of it.toDtoV1()) }
                .mapFailure { it.toResponse() }
                .get()
        }
    }

    val get = "/posts" / PostId.lens meta {
        operationId = "getPost"
        summary = "Get Post"
        tags += tag

        returning(OK, PostDtoV1.lens to PostDtoV1.sample)
        returning(NOT_FOUND to "post not found")
    } bindContract GET to { postId ->
        {
            service.getPost(postId)
                .map { Response(OK).with(PostDtoV1.lens of it.toDtoV1()) }
                .mapFailure { it.toResponse() }
                .get()
        }
    }

    val delete = "/posts" / PostId.lens meta {
        operationId = "deletePost"
        summary = "Delete Post"
        tags += tag
        security = bearerAuth

        returning(OK, PostDtoV1.lens to PostDtoV1.sample)
        returning(NOT_FOUND to "post not found")
    } bindContract DELETE to { postId ->
        { request ->
            service.deletePost(auth(request).id, postId)
                .map { Response(OK).with(PostDtoV1.lens of it.toDtoV1()) }
                .mapFailure { it.toResponse() }
                .get()
        }
    }

    return listOf(create, list, listByAuthor, get, edit, delete)
}