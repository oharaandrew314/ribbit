package com.ribbit

import org.http4k.core.Response
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Status.Companion.NOT_FOUND
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
    is PostNotFound -> Response(NOT_FOUND)
    is SubNotFound -> Response(NOT_FOUND)
    is DuplicateSub -> Response(CONFLICT)
    is UserNotFound -> Response(NOT_FOUND)
    is CannotEditPost -> Response(FORBIDDEN)
    UserAlreadyExists -> Response(CONFLICT)
    is CannotCreatePost -> Response(FORBIDDEN)
    is DuplicateUsername -> Response(CONFLICT)
    CannotCreateSub -> Response(FORBIDDEN)
    ProfileNotCreated -> Response(NOT_FOUND)
}.with(RibbitErrorDto.lens of RibbitErrorDto(code = javaClass.simpleName, details = message))