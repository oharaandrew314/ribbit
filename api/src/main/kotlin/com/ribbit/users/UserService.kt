package com.ribbit.users

import com.ribbit.IdpFailure
import com.ribbit.RibbitError
import com.ribbit.UserNotFound
import com.ribbit.auth.AccessToken
import com.ribbit.auth.Authorizer
import com.ribbit.auth.Issuer
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr

class UserService(
    private val users: UserRepo,
    private val issuer: Issuer,
    private val googleAuth: Authorizer
) {

    fun loginWithGoogle(googleIdToken: AccessToken): Result4k<AccessToken, RibbitError> {
        val principal = googleAuth.authorize(googleIdToken) ?: return Failure(IdpFailure)
        users += principal

        return Success(issuer.issue(principal))
    }

    fun createUser(id: UserId, name: String): User {
        val user = User(id = id, name = name)
        users += user
        return user
    }

    fun getUser(id: UserId): Result4k<User, RibbitError> {
        return users[id].asResultOr { UserNotFound(id) }
    }
}