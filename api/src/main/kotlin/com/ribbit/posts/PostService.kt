package com.ribbit.posts

import com.github.ksuid.KsuidGenerator
import com.ribbit.CannotCreatePost
import com.ribbit.CannotEditPost
import com.ribbit.PostNotFound
import com.ribbit.RibbitError
import com.ribbit.SubNotFound
import com.ribbit.core.Cursor
import com.ribbit.subs.SubId
import com.ribbit.subs.SubRepo
import com.ribbit.users.EmailHash
import com.ribbit.users.Username
import com.ribbit.users.UserRepo
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek
import io.andrewohara.utils.result.failIf
import java.time.Clock

class PostService(
    val repo: PostRepo,
    private val subs: SubRepo,
    private val users: UserRepo,
    private val clock: Clock,
    private val ksuidGen: KsuidGenerator
) {
    fun createPost(principal: EmailHash, subId: SubId, data: PostData): Result4k<Post, RibbitError> {
        val user = users[principal] ?: return Failure(CannotCreatePost(subId))
        subs[subId] ?: return Failure(SubNotFound(subId))

        val post = Post(
            id = PostId.of(ksuidGen.newKsuid(clock.instant())),
            subId = subId,
            authorName = user.name,
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

    fun getPosts(id: SubId,  limit: Int, cursor: PostId? = null): Result4k<Cursor<Post, PostId>, RibbitError> {
        return Success(repo[id, limit, cursor])
    }

    fun getPosts(authorName: Username, limit: Int, cursor: PostId? = null): Result4k<Cursor<Post, PostId>, RibbitError> {
        return Success(repo[authorName, limit, cursor])
    }

    fun deletePost(principal: EmailHash, id: PostId): Result4k<Post, RibbitError> {
        val user = users[principal] ?: return Failure(CannotEditPost(id))

        return repo[id]
            .asResultOr { PostNotFound(id) }
            .failIf({it.authorName != user.name}, { CannotEditPost(id) })
            .peek { repo -= it }
    }

    fun editPost(principal: EmailHash, id: PostId, data: PostData): Result4k<Post, RibbitError> {
        val user = users[principal] ?: return Failure(CannotEditPost(id))

        return repo[id]
            .asResultOr { PostNotFound(id) }
            .failIf({it.authorName != user.name}, { CannotEditPost(id) })
            .map { it.update(data, clock.instant()) }
            .peek { repo += it }
    }
}