package com.ribbit.posts

import com.ribbit.subs.SubId
import com.ribbit.users.Username
import io.andrewohara.utils.ksuid.instant
import se.ansman.kotshi.JsonSerializable
import java.time.Instant

@JsonSerializable
data class Post(
    val id: PostId,
    val authorName: Username,
    val subId: SubId,
    val title: String,
    val content: String,
    val updated: Instant?
) {
    val created get() = id.instant
}

fun Post.update(data: PostData, time: Instant) = copy(
    title = data.title,
    content = data.content,
    updated = time
)