package com.ribbit.posts

import com.ribbit.core.RibbitError
import com.ribbit.core.UserId
import com.ribbit.subs.SubId
import com.ribbit.subs.SubService
import com.ribbit.subs.subNotFound
import com.ribbit.users.UserService
import com.ribbit.users.userNotFound
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.peek
import org.http4k.cloudnative.env.Environment
import java.time.Clock

class PostService internal constructor(
    private val posts: PostRepo,
    private val users: UserService,
    private val subs: SubService,
    private val clock: Clock
) {

    fun createPost(userId: UserId, subId: SubId, data: PostData): Result4k<Post, RibbitError> {
        subs.getSub(subId).onFailure { return it }

        return data.newPost(userId, subId, clock.instant())
            .also { posts += it }
            .let { Success(it) }
    }

    fun getPost(id: PostId): Result4k<Post, RibbitError> {
        return posts[id].asResultOr { postNotFound(id) }
    }

    fun getPosts(id: UserId): Result4k<List<Post>, RibbitError> {
        return users.getUser(id)
            .asResultOr { userNotFound(id) }
            .map { posts[id] }
    }

    fun getPosts(id: SubId): Result4k<List<Post>, RibbitError> {
        return subs.getSub(id)
            .asResultOr { subNotFound(id) }
            .map { posts[id] }
    }

    fun deletePost(id: PostId): Result4k<Post, RibbitError> {
        return posts[id]
            .asResultOr { postNotFound(id) }
            .peek { posts -= it }
    }

    fun editPost(id: PostId, data: PostData): Result4k<Post, RibbitError> {
        return posts[id]
            .asResultOr { postNotFound(id) }
            .map { it.update(data, clock.instant()) }
            .peek { posts += it }
    }
}

fun postService(env: Environment, clock: Clock, users: UserService, subs: SubService): PostService {
    requireNotNull(env)
    return PostService(PostRepo(), users, subs, clock)
}