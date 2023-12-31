package com.ribbit.subs.api

import com.ribbit.RibbitErrorDto
import com.ribbit.core.CursorDtoV1
import com.ribbit.posts.lens
import com.ribbit.subs.SubId
import com.ribbit.subs.SubService
import com.ribbit.toResponse
import com.ribbit.users.EmailHash
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
import org.http4k.lens.Query
import org.http4k.lens.RequestContextLens
import org.http4k.lens.value

fun subsApiV1(service: SubService, auth: RequestContextLens<EmailHash>, bearerAuth: Security): List<ContractRoute> {
    val tag = Tag("Subribbits")
    val cursorLens = Query.value(SubId).optional("cursor")

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
        queries += listOf(cursorLens, CursorDtoV1.limitLens)

        returning(OK, SubCursorDtoV1.lens to SubCursorDtoV1.sample)
    } bindContract GET to { req ->
        val page = service.list(limit = CursorDtoV1.limitLens(req), cursor = cursorLens(req))
        Response(OK).with(SubCursorDtoV1.lens of page.toDtoV1())
    }

    return listOf(get, create, list)
}