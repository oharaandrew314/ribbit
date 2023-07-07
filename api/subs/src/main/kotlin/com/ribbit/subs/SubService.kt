package com.ribbit.subs

import com.ribbit.core.RibbitError
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr
import org.http4k.cloudnative.env.Environment

class SubService internal constructor(private val subs: SubRepo) {

    fun getSub(id: SubId): Result4k<Sub, RibbitError> {
        return subs[id].asResultOr { subNotFound(id) }
    }

    fun createSub(data: SubData): Result4k<Sub, RibbitError> {
        if (subs[data.id] != null) return Failure(duplicateSub(data.id))

        val sub = Sub(
            id = data.id,
            name = data.name
        )

        subs += sub

        return Success(sub)
    }
}

fun subService(env: Environment): SubService {
    requireNotNull(env)
    return SubService(SubRepo())
}