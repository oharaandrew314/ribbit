package com.ribbit.posts

import com.ribbit.subs.SubId
import com.ribbit.users.UserId

data class Post(
    val id: PostId,
    val author: UserId,
    val sub: SubId,
    val title: String,
    val content: String
)