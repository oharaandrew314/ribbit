package com.ribbit.users.api

import com.ribbit.ribbitJson
import com.ribbit.users.User
import com.ribbit.users.UserData
import com.ribbit.users.UserId
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class UserDtoV1(
    val id: UserId,
    val name: String
) {
    companion object {
        val lens = ribbitJson.autoBody<UserDtoV1>().toLens()
        val sample = UserDtoV1(
            id = UserId.of("user1"),
            name = "userone"
        )
    }
}

@JsonSerializable
data class UserDataDtoV1(
    val name: String
) {
    companion object {
        val lens = ribbitJson.autoBody<UserDataDtoV1>().toLens()
        val sample = UserDataDtoV1(
            name = "user_one"
        )
    }
}

fun User.toDtoV1() = UserDtoV1(
    id = id,
    name = name
)

fun UserDataDtoV1.toModel() = UserData(
    name = name
)