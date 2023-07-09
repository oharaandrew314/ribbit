package com.ribbit.posts

import com.ribbit.subs.SubId
import com.ribbit.users.UserId
import org.http4k.lens.Path
import org.http4k.lens.value

private val userIdLens = Path.value(UserId).of("user_id")
private val postIdLens = Path.value(PostId).of("post_id")
private val subIdLens = Path.value(SubId).of("sub_id")

val UserId.Companion.lens get() = userIdLens
val PostId.Companion.lens get() = postIdLens
val SubId.Companion.lens get() = subIdLens