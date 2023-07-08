package com.ribbit.users

import com.ribbit.core.AccessToken
import com.ribbit.core.User
import com.ribbit.core.UserId

fun UserService.create(
    id: String,
    name: String = "user$id"
): Pair<User, AccessToken> {
    val principal = User(
        id = UserId.of(id),
        name = name
    )
    users += principal
    val token = issuer.issue(principal)

    return principal to token
}