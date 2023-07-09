package com.ribbit.auth

import com.ribbit.users.User

fun interface Authorizer {
    fun authorize(token: AccessToken): User?
}

interface Issuer {
    fun issue(principal: User): AccessToken
}

