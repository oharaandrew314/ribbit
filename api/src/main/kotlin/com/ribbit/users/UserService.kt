package com.ribbit.users

import com.ribbit.UserAlreadyExists
import com.ribbit.RibbitError
import com.ribbit.UserNotFound
import com.ribbit.DuplicateUsername
import com.ribbit.ProfileNotCreated
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr

class UserService(val repo: UserRepo) {

    fun getUser(name: Username): Result4k<User, RibbitError> {
        return repo[name].asResultOr { UserNotFound(name) }
    }

    fun getUser(emailHash: EmailHash): Result4k<User, RibbitError> {
        return repo[emailHash].asResultOr { ProfileNotCreated }
    }

    fun create(emailHash: EmailHash, data: UserData): Result4k<User, RibbitError> {
        if (repo[emailHash] != null) return Failure(UserAlreadyExists)
        if (repo[data.name] != null) return Failure(DuplicateUsername(data.name))

        val user = User(
            emailHash = emailHash,
            name = data.name
        )
        repo += user

        return Success(user)
    }
}