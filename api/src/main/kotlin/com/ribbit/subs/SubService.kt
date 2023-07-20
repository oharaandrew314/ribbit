package com.ribbit.subs

import com.ribbit.CannotCreateSub
import com.ribbit.DuplicateSub
import com.ribbit.RibbitError
import com.ribbit.SubNotFound
import com.ribbit.users.EmailHash
import com.ribbit.users.UserRepo
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr

class SubService(val subs: SubRepo, val users: UserRepo) {

    fun getSub(id: SubId): Result4k<Sub, RibbitError> {
        return subs[id].asResultOr { SubNotFound(id) }
    }

    fun createSub(principal: EmailHash, data: SubData): Result4k<Sub, RibbitError> {
        val user = users[principal] ?: return Failure(CannotCreateSub)
        if (subs[data.id] != null) return Failure(DuplicateSub(data.id))

        val sub = Sub(
            id = data.id,
            name = data.name,
            owner = user.name
        )

        subs += sub

        return Success(sub)
    }

    fun list(limit: Int, cursor: SubId? = null) = subs.list(limit, cursor)
}