package com.ribbit.posts

import com.ribbit.core.RibbitError
import com.ribbit.subs.SubId
import com.ribbit.subs.SubNotFound
import com.ribbit.subs.SubService
import com.ribbit.users.UserId
import com.ribbit.users.UserNotFound
import com.ribbit.users.UserService
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.peek
import org.http4k.cloudnative.env.Environment

class PostService internal constructor(
    private val posts: PostRepo,
    private val users: UserService,
    private val subs: SubService
) {

    fun createPost(userId: UserId, subId: SubId, data: PostData): Result4k<Post, RibbitError> {
        users.getUser(userId).onFailure { return it }
        subs.getSub(subId).onFailure { return it }

        val post = Post(
            id = PostId.next(),
            sub = subId,
            author = userId,
            title = data.title,
            content = data.content
        )

        posts += post

        return Success(post)
    }

    fun getPost(id: PostId): Result4k<Post, PostNotFound> {
        return posts[id].asResultOr { PostNotFound(id) }
    }

    fun getPosts(id: UserId): Result4k<List<Post>, UserNotFound> {
        return users.getUser(id)
            .asResultOr { UserNotFound(id) }
            .map { posts[id] }
    }

    fun getPosts(id: SubId): Result4k<List<Post>, SubNotFound> {
        return subs.getSub(id)
            .asResultOr { SubNotFound(id) }
            .map { posts[id] }
    }

    fun deletePost(id: PostId): Result4k<Post, PostNotFound> {
        return posts[id]
            .asResultOr { PostNotFound(id) }
            .peek { posts -= it }
    }
}

fun postService(env: Environment, users: UserService, subs: SubService): PostService {
    requireNotNull(env)
    return PostService(PostRepo(), users, subs)
}