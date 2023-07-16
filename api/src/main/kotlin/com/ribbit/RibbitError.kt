package com.ribbit

import com.ribbit.posts.PostId
import com.ribbit.subs.SubId
import com.ribbit.users.Username

sealed class RibbitError(val message: String)

data object CannotCreateSub: RibbitError("Not authorized to create a sub")
data class CannotCreatePost(val subId: SubId): RibbitError("Not authorized to create post in sub $subId")
data class CannotEditPost(val id: PostId): RibbitError("Not authorized to edit post $id")
data class PostNotFound(val id: PostId): RibbitError("Post $id not found")
data class SubNotFound(val id: SubId): RibbitError("Sub $id not found")
data class DuplicateSub(val id: SubId): RibbitError("Duplicate sub: $id")
data class UserNotFound(val name: Username): RibbitError("User $name not found")
data class DuplicateUsername(val name: Username): RibbitError("Username $name already taken")
data object CannotChangeUsername: RibbitError("Cannot change your username")
