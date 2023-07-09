package com.ribbit.subs

import com.ribbit.DuplicateSub
import com.ribbit.RibbitError
import com.ribbit.SubNotFound
import com.ribbit.users.UserId
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr

class SubService(val subs: SubRepo) {

    fun getSub(id: SubId): Result4k<Sub, RibbitError> {
        return subs[id].asResultOr { SubNotFound(id) }
    }

    fun createSub(owner: UserId, data: SubData): Result4k<Sub, RibbitError> {
        if (subs[data.id] != null) return Failure(DuplicateSub(data.id))

        val sub = Sub(
            id = data.id,
            name = data.name,
            owner = owner
        )

        subs += sub

        return Success(sub)
    }
}