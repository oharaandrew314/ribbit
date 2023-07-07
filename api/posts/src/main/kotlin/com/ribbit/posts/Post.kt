package com.ribbit.posts

import com.ribbit.core.UserId
import com.ribbit.subs.SubId
import java.time.Instant

data class Post(
    val id: PostId,
    val author: UserId,
    val sub: SubId,
    val title: String,
    val content: String,
    val created: Instant,
    val updated: Instant?
)

fun PostData.newPost(author: UserId, sub: SubId, time: Instant) = Post(
    id = PostId.next(),
    sub = sub,
    author = author,
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