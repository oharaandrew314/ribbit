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
    val created: Instant,
    val updated: Instant?
)

fun PostData.newPost(author: UserId, sub: SubId, time: Instant) = Post(
    id = PostId.next(),
    subId = sub,
    authorId = author,
    title = title,
    content = content,
    created = time,
    updated = null
)

fun Post.update(data: PostData, time: Instant) = copy(
    title = data.title,
    content = data.content,
    updated = time
)