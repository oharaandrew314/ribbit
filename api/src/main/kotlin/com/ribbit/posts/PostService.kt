package com.ribbit.posts

import com.ribbit.CannotEditPost
import com.ribbit.PostNotFound
import com.ribbit.RibbitError
import com.ribbit.subs.SubId
import com.ribbit.subs.SubService
import com.ribbit.users.UserId
import com.ribbit.users.UserService
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.peek
import io.andrewohara.utils.result.failIf
import java.time.Clock

class PostService(
    val posts: PostRepo,
    private val subs: SubService,
    private val users: UserService,
    private val clock: Clock,
    private val pageSize: Int,
) {
    fun createPost(userId: UserId, subId: SubId, data: PostData): Result4k<Post, RibbitError> {
        subs.getSub(subId).onFailure { return it }

        return data.newPost(userId, subId, clock.instant())
            .also { posts += it }
            .let { Success(it) }
    }

    fun getPost(postId: PostId): Result4k<Post, RibbitError> {
        return posts[postId].asResultOr { PostNotFound(postId) }
    }

    fun getPosts(id: SubId): Result4k<List<Post>, RibbitError> {
        subs.getSub(id).onFailure { return it }

        return posts[id]
            .take(pageSize)
            .toList()
            .let(::Success)
    }

    fun getPosts(authorId: UserId): Result4k<List<Post>, RibbitError> {
        users.getUser(authorId).onFailure { return it }

        return posts[authorId]
            .take(pageSize)
            .toList()
            .let(::Success)
    }

    fun deletePost(editor: UserId, id: PostId): Result4k<Post, RibbitError> {
        return posts[id]
            .asResultOr { PostNotFound(id) }
            .failIf({it.authorId != editor}, { CannotEditPost(id) })
            .peek { posts -= it }
    }

    fun editPost(editor: UserId, id: PostId, data: PostData): Result4k<Post, RibbitError> {
        return posts[id]
            .asResultOr { PostNotFound(id) }
            .failIf({it.authorId != editor}, { CannotEditPost(id) })
            .map { it.update(data, clock.instant()) }
            .peek { posts += it }
    }
}