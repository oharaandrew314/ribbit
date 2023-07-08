package com.ribbit.core

import org.http4k.core.Response
import org.http4k.core.Status

data class RibbitError(val status: Status, val message: String)

fun unauthorized() = RibbitError(Status.UNAUTHORIZED, "authorization is required")
fun RibbitError.toResponse() = Response(status).body(message)