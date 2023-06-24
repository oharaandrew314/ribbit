package com.ribbit.users

import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.asResultOr
import org.http4k.cloudnative.env.Environment

class UserService internal constructor(private val users: UserRepo) {

    fun getUser(id: UserId): Result4k<User, UserNotFound> {
        return users[id].asResultOr { UserNotFound(id) }
    }
}

fun userService(env: Environment): UserService {
    requireNotNull(env)
    return UserService(UserRepo())
}