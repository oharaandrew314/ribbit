package com.ribbit.posts

import com.github.ksuid.KsuidGenerator
import com.ribbit.CannotEditPost
import com.ribbit.PostNotFound
import com.ribbit.RibbitError
import com.ribbit.core.Cursor
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
    val repo: PostRepo,
    private val subs: SubService,
    private val users: UserService,
    private val clock: Clock,
    private val ksuidGen: KsuidGenerator
) {
    fun createPost(userId: UserId, subId: SubId, data: PostData): Result4k<Post, RibbitError> {
        subs.getSub(subId).onFailure { return it }

        val post = Post(
            id = PostId.of(ksuidGen.newKsuid(clock.instant())),
            subId = subId,
            authorId = userId,
            title = data.title,
            content = data.content,
            updated = null
        )

        repo += post

        return Success(post)
    }

    fun getPost(postId: PostId): Result4k<Post, RibbitError> {
        return repo[postId].asResultOr { PostNotFound(postId) }
    }

    fun getPosts(id: SubId, cursor: PostId? = null): Result4k<Cursor<Post, PostId>, RibbitError> {
        return subs.getSub(id)
            .map { repo[id, cursor] }
    }

    fun getPosts(authorId: UserId, cursor: PostId? = null): Result4k<Cursor<Post, PostId>, RibbitError> {
        return users.getUser(authorId)
            .map { repo[authorId, cursor] }
    }

    fun deletePost(editor: UserId, id: PostId): Result4k<Post, RibbitError> {
        return repo[id]
            .asResultOr { PostNotFound(id) }
            .failIf({it.authorId != editor}, { CannotEditPost(id) })
            .peek { repo -= it }
    }

    fun editPost(editor: UserId, id: PostId, data: PostData): Result4k<Post, RibbitError> {
        return repo[id]
            .asResultOr { PostNotFound(id) }
            .failIf({it.authorId != editor}, { CannotEditPost(id) })
            .map { it.update(data, clock.instant()) }
            .peek { repo += it }
    }
}