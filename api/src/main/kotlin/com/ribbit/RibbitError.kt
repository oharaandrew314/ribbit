package com.ribbit

import com.ribbit.posts.PostId
import com.ribbit.subs.SubId
import com.ribbit.users.UserId

sealed class RibbitError(val message: String)

data class CannotEditPost(val id: PostId): RibbitError("Not authorized to edit post $id")
data class PostNotFound(val id: PostId): RibbitError("Post $id not found")
data class SubNotFound(val id: SubId): RibbitError("Sub $id not found")
data class DuplicateSub(val id: SubId): RibbitError("Duplicate sub: $id")
data class UserNotFound(val id: UserId): RibbitError("User $id not found")
