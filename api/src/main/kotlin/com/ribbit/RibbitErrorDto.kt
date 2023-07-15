package com.ribbit

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class RibbitErrorDto(val code: String, val details: String) {
    companion object {
        val lens = ribbitJson.autoBody<RibbitErrorDto>().toLens()
        val sample = RibbitErrorDto(
            code = "Glitch",
            details = "glitch in the system"
        )
    }
}

fun RibbitError.toResponse() = when(this) {
    is PostNotFound -> Response(Status.NOT_FOUND)
    is SubNotFound -> Response(Status.NOT_FOUND)
    is DuplicateSub -> Response(Status.CONFLICT)
    is UserNotFound -> Response(Status.NOT_FOUND)
    is CannotEditPost -> Response(Status.FORBIDDEN)
}.with(RibbitErrorDto.lens of RibbitErrorDto(code = javaClass.simpleName, details = message))