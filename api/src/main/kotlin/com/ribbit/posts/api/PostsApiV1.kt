package com.ribbit.posts.api

import com.ribbit.core.CursorDtoV1
import com.ribbit.posts.PostId
import com.ribbit.posts.PostService
import com.ribbit.posts.lens
import com.ribbit.subs.SubId
import com.ribbit.toResponse
import com.ribbit.users.EmailHash
import com.ribbit.users.Username
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
import org.http4k.lens.Query
import org.http4k.lens.RequestContextLens
import org.http4k.lens.value

fun postsApiV1(service: PostService, auth: RequestContextLens<EmailHash>, bearerAuth: Security): List<ContractRoute> {
    val tag = Tag("Posts")
    val cursorLens = Query.value(PostId).optional("cursor")

    val create = "/subs" / SubId.lens / "posts" meta {
        operationId = "createPost"
        summary = "Create Post"
        tags += tag
        security = bearerAuth

        receiving(PostDataDtoV1.lens to PostDataDtoV1.sample)
        returning(OK, PostDtoV1.lens to PostDtoV1.sample)
    } bindContract POST to { subId, _ ->
        { request ->
            service.createPost(auth(request), subId, PostDataDtoV1.lens(request).toModel())
                .map { Response(OK).with(PostDtoV1.lens of it.toDtoV1()) }
                .mapFailure { it.toResponse() }
                .get()
        }
    }

    val list = "/subs" / SubId.lens / "posts" meta {
        operationId = "listSubPosts"
        summary = "List Posts for Sub"
        queries += listOf(cursorLens, CursorDtoV1.limitLens)
        tags += tag

        returning(OK, PostDtoV1.manyLens to PostDtoV1.sampleCursor)
        returning(NOT_FOUND to "sub not found")
    } bindContract GET to { subId, _ ->
        { req ->
            service.getPosts(subId, limit = CursorDtoV1.limitLens(req), cursor = cursorLens(req))
                .map { Response(OK).with(PostDtoV1.manyLens of it.toDtoV1()) }
                .mapFailure { it.toResponse() }
                .get()
        }
    }

    val listByAuthor = "/users" / Username.lens / "posts" meta {
        operationId = "listPostsByAuthor"
        summary = "List Posts for Author"
        tags += tag
        queries += listOf(cursorLens, CursorDtoV1.limitLens)

        returning(OK, PostDtoV1.manyLens to PostDtoV1.sampleCursor)
    } bindContract GET to { userId, _ ->
        { req ->
            service.getPosts(userId, limit = CursorDtoV1.limitLens(req), cursor = cursorLens(req))
                .map { Response(OK).with(PostDtoV1.manyLens of it.toDtoV1()) }
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
            service.editPost(auth(request), postId, PostDataDtoV1.lens(request).toModel())
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
            service.deletePost(auth(request), postId)
                .map { Response(OK).with(PostDtoV1.lens of it.toDtoV1()) }
                .mapFailure { it.toResponse() }
                .get()
        }
    }

    return listOf(create, list, listByAuthor, get, edit, delete)
}