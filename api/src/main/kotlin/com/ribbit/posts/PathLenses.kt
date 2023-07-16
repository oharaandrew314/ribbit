package com.ribbit.posts

import com.ribbit.subs.SubId
import com.ribbit.users.Username
import org.http4k.lens.Path
import org.http4k.lens.value

private val userNameLens = Path.value(Username).of("user_id")
private val postIdLens = Path.value(PostId).of("post_id")
private val subIdLens = Path.value(SubId).of("sub_id")

val Username.Companion.lens get() = userNameLens
val PostId.Companion.lens get() = postIdLens
val SubId.Companion.lens get() = subIdLens