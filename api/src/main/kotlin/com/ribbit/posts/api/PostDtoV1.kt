package com.ribbit.posts.api

import com.github.ksuid.Ksuid
import com.ribbit.core.Cursor
import com.ribbit.core.CursorDtoV1
import com.ribbit.posts.Post
import com.ribbit.posts.PostId
import com.ribbit.ribbitJson
import com.ribbit.subs.SubId
import com.ribbit.users.Username
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

@JsonSerializable
data class PostDtoV1(
    val id: PostId,
    val authorName: Username,
    val subId: SubId,
    val title: String,
    val content: String,
    val created: Instant,
    val updated: Instant?
) {
    companion object {
        val lens = ribbitJson.autoBody<PostDtoV1>().toLens()
        val manyLens = ribbitJson.autoBody<PostCursorDtoV1>().toLens()

        val sample = PostDtoV1(
            id = PostId.of(Ksuid.fromString("2SCJo6TLReBpeVSFPmkYAyw7aKi")),
            authorName = Username.of("user1"),
            subId = SubId.of("frogs"),
            title = "Frogs are cool",
            content = "Super cool",
            created = Instant.EPOCH,
            updated = Instant.MAX
        )
        val sampleCursor = PostCursorDtoV1(
            items = listOf(sample),
            next = "next_token"
        )
    }
}

@JsonSerializable
data class PostCursorDtoV1(
    override val items: List<PostDtoV1>,
    override val next: String?
): CursorDtoV1<PostDtoV1>

fun Post.toDtoV1() = PostDtoV1(
    id = id,
    authorName = authorName,
    subId = subId,
    title = title,
    content = content,
    created = created,
    updated = updated
)

fun Cursor<Post, PostId>.toDtoV1() = PostCursorDtoV1(
    items = items.map(Post::toDtoV1),
    next = next?.value?.toString()
)
