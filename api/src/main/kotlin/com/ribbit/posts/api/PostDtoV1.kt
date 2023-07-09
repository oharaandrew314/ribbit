package com.ribbit.posts.api

import com.ribbit.posts.Post
import com.ribbit.posts.PostId
import com.ribbit.ribbitJson
import com.ribbit.subs.SubId
import com.ribbit.users.UserId
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

@JsonSerializable
data class PostDtoV1(
    val id: PostId,
    val authorId: UserId,
    val subId: SubId,
    val title: String,
    val content: String,
    val created: Instant,
    val updated: Instant?
) {
    companion object {
        val lens = ribbitJson.autoBody<PostDtoV1>().toLens()
        val manyLens = ribbitJson.autoBody<Array<PostDtoV1>>().toLens()

        val sample = PostDtoV1(
            id = PostId.next(),
            authorId = UserId.of("user1"),
            subId = SubId.of("frogs"),
            title = "Frogs are cool",
            content = "Super cool",
            created = Instant.EPOCH,
            updated = Instant.MAX
        )
    }
}

internal fun Post.toDtoV1() = PostDtoV1(
    id = id,
    authorId = authorId,
    subId = subId,
    title = title,
    content = content,
    created = created,
    updated = updated
)