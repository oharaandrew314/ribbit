package com.ribbit.posts

import com.ribbit.core.RibbitError
import org.http4k.core.Status.Companion.NOT_FOUND

fun postNotFound(id: PostId) = RibbitError(NOT_FOUND, "Post $id not found")