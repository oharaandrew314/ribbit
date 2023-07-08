package com.ribbit.subs

import com.ribbit.core.RibbitError
import com.ribbit.core.User
import com.ribbit.core.UserId
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.Http
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.core.HttpHandler
import org.http4k.lens.value

class SubService internal constructor(private val subs: SubRepo) {

    fun getSub(id: SubId): Result4k<Sub, RibbitError> {
        return subs[id].asResultOr { subNotFound(id) }
    }

    fun createSub(owner: UserId, data: SubData): Result4k<Sub, RibbitError> {
        if (subs[data.id] != null) return Failure(duplicateSub(data.id))

        val sub = Sub(
            id = data.id,
            name = data.name,
            owner = owner
        )

        subs += sub

        return Success(sub)
    }
}

val subsTableName = EnvironmentKey.value(TableName).required("SUBS_TABLE_NAME")

fun subService(env: Environment, internet: HttpHandler): SubService {
    val dynamo = DynamoDb.Http(env, http = internet)
    val subs = SubRepo(dynamo.subsTable(env[subsTableName]))
    return SubService(subs)
}