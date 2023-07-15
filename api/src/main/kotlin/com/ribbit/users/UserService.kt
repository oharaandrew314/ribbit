package com.ribbit.users

import com.ribbit.RibbitError
import com.ribbit.UserNotFound
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.asResultOr
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek

class UserService(val repo: UserRepo) {

    fun getUser(id: UserId): Result4k<User, RibbitError> {
        return repo[id].asResultOr { UserNotFound(id) }
    }

    fun updateUser(id: UserId, data: UserData): Result4k<User, UserNotFound> {
        return repo[id]
            .asResultOr { UserNotFound(id) }
            .map { it.copy(name = data.name) }
            .peek(repo::plusAssign)
    }
}