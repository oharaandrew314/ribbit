package com.ribbit.users.api

import com.ribbit.users.User
import com.ribbit.users.UserId
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
            name = "user_one"
        )
    }
}

fun User.toDtoV1() = UserDtoV1(
    id = id,
    name = name
)