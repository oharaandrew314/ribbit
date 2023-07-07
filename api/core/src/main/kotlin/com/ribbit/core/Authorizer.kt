package com.ribbit.core

fun interface Authorizer {
    fun authorize(token: AccessToken): User?
}

interface Issuer {
    fun issue(principal: User): AccessToken
}

