package com.ribbit.posts

import com.ribbit.core.RibbitError

data class PostNotFound(val id: PostId): RibbitError(404, "Post $id not found")