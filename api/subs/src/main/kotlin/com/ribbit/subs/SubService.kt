package com.ribbit.subs

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr
import org.http4k.cloudnative.env.Environment

class SubService internal constructor(private val subs: SubRepo) {

    fun getSub(id: SubId): Result4k<Sub, SubNotFound> {
        return subs[id].asResultOr { SubNotFound(id) }
    }

    fun getSub(name: SubName): Result4k<Sub, SubNameNotFound> {
        return subs[name].asResultOr { SubNameNotFound(name) }
    }

    fun create(data: SubData): Result4k<Sub, DuplicateSub> {
        if (subs[data.name] != null) return Failure(DuplicateSub(data.name))

        val sub = Sub(
            id = SubId.next(),
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