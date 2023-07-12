package com.ribbit.posts

import com.ribbit.subs.SubId
import com.ribbit.users.UserId
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

@JsonSerializable
data class Post(
    val id: PostId,
    val authorId: UserId,
    val subId: SubId,
    val title: String,
    val content: String,
    val updated: Instant?
) {
    val created get() = id.time
}

fun Post.update(data: PostData, time: Instant) = copy(
    title = data.title,
    content = data.content,
    updated = time
)