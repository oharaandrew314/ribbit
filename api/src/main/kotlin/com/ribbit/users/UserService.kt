package com.ribbit.users

import com.ribbit.RibbitError
import com.ribbit.UserNotFound
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.asResultOr

class UserService(private val users: UserRepo) {

    fun createUser(id: UserId, name: String): User {
        val user = User(id = id, name = name)
        users += user
        return user
    }

    fun getUser(id: UserId): Result4k<User, RibbitError> {
        return users[id].asResultOr { UserNotFound(id) }
    }
}