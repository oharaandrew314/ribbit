package com.ribbit.users

import com.ribbit.core.RibbitError

data class UserNotFound(val id: UserId): RibbitError(404, "User $id not found")