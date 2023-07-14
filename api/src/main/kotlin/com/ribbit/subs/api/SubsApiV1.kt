package com.ribbit.subs.api

import com.ribbit.RibbitErrorDto
import com.ribbit.posts.lens
import com.ribbit.subs.SubId
import com.ribbit.subs.SubService
import com.ribbit.toResponse
import com.ribbit.users.UserId
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
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.RequestContextLens

fun subsApiV1(service: SubService, auth: RequestContextLens<UserId>, bearerAuth: Security): List<ContractRoute> {
    val tag = Tag("Subribbits")

    val get = "/subs" / SubId.lens meta {
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
        returning(CONFLICT, RibbitErrorDto.lens to RibbitErrorDto.sample)
    } bindContract POST to { request ->
        service.createSub(auth(request), SubDataDtoV1.lens(request).toModel())
            .map { Response(OK).with(SubDtoV1.lens of it.toDtoV1()) }
            .mapFailure { it.toResponse() }
            .get()
    }

    val list = "/subs" meta {
        operationId = "listSubs"
        summary = "List Subs"
        tags += tag

        returning(OK, SubCursorDtoV1.lens to SubCursorDtoV1.sample)
    } bindContract GET to { _ ->
        Response(OK).with(SubCursorDtoV1.lens of service.list().toDtoV1())
    }

    return listOf(get, create, list)
}