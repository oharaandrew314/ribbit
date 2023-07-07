package com.ribbit.users.api

import com.ribbit.core.User
import com.ribbit.core.UserId
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class UserDtoV1(
    val id: UserId,
    val name: String
) {
    companion object {
        val lens = usersJson.autoBody<UserDtoV1>().toLens()
        val sample = UserDtoV1(
            id = UserId.of("user1"),
            name = "userone"
        )
    }
}

internal fun User.toDtoV1() = UserDtoV1(
    id = id,
    name = name
)