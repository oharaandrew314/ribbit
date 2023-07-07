package com.ribbit.posts.api

import com.ribbit.core.UserId
import com.ribbit.posts.Post
import com.ribbit.posts.PostId
import com.ribbit.subs.SubId
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class PostDtoV1(
    val id: PostId,
    val author: UserId,
    val sub: SubId,
    val title: String,
    val content: String
) {
    companion object {
        val lens = postsJson.autoBody<PostDtoV1>().toLens()
        val manyLens = postsJson.autoBody<Array<PostDtoV1>>().toLens()

        val sample = PostDtoV1(
            id = PostId.next(),
            author = UserId.of("user1"),
            sub = SubId.of("frogs"),
            title = "Frogs are cool",
            content = "Super cool"
        )
    }
}

internal fun Post.toDtoV1() = PostDtoV1(
    id = id,
    author = author,
    sub = sub,
    title = title,
    content = content
)