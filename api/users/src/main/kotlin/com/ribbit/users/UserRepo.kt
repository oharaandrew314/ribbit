package com.ribbit.users

internal class UserRepo {
    private val users = mutableMapOf<UserId, User>()

    operator fun get(id: UserId) = users[id]

    operator fun plusAssign(user: User) {
        users[user.id] = user
    }
    operator fun minusAssign(user: User) {
        users.remove(user.id)
    }
}