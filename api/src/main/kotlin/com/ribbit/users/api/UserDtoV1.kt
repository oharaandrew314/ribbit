package com.ribbit.users.api

import com.ribbit.ribbitJson
import com.ribbit.users.User
import com.ribbit.users.UserData
import com.ribbit.users.Username
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class UserDtoV1(
    val name: Username
) {
    companion object {
        val lens = ribbitJson.autoBody<UserDtoV1>().toLens()
        val sample = UserDtoV1(
            name = Username.of("user_one")
        )
    }
}

@JsonSerializable
data class UserDataDtoV1(
    val name: Username
) {
    companion object {
        val lens = ribbitJson.autoBody<UserDataDtoV1>().toLens()
        val sample = UserDataDtoV1(
            name = Username.of("user_one")
        )
    }
}

fun User.toDtoV1() = UserDtoV1(
    name = name
)

fun UserDataDtoV1.toModel() = UserData(
    name = name
)