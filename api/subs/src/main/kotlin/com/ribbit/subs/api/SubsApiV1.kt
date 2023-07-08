package com.ribbit.subs.api

import com.ribbit.core.User
import com.ribbit.core.toResponse
import com.ribbit.subs.SubId
import com.ribbit.subs.SubService
import dev.forkhandles.result4k.get
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import org.http4k.contract.ContractRoute
import org.http4k.contract.Tag
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.contract.security.Security
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.Path
import org.http4k.lens.RequestContextLens
import org.http4k.lens.value

fun subsApiV1(service: SubService, auth: RequestContextLens<User>, bearerAuth: Security): List<ContractRoute> {
    val tag = Tag("Subribbits")
    val subIdLens = Path.value(SubId).of("sub_id")

    val get = "/subs" / subIdLens meta {
        operationId = "getSubV1"
        summary = "Get Sub"
        tags += tag

        returning(OK, SubDtoV1.lens to SubDtoV1.sample)
    } bindContract GET to { subId ->
        {
            service.getSub(subId)
                .map { Response(OK).with(SubDtoV1.lens of it.toDtoV1()) }
                .mapFailure { it.toResponse() }
                .get()
        }
    }

    val create = "/subs" meta {
        operationId = "createSub"
        summary = "Create Sub"
        tags += tag
        security = bearerAuth

        receiving(SubDataDtoV1.lens to SubDataDtoV1.sample)
        returning(OK, SubDtoV1.lens to SubDtoV1.sample)
    } bindContract POST to { request ->
        service.createSub(auth(request).id, SubDataDtoV1.lens(request).toModel())
            .map { Response(OK).with(SubDtoV1.lens of it.toDtoV1()) }
            .mapFailure { it.toResponse() }
            .get()
    }

    return listOf(get, create)
}