package com.ribbit.users

import com.ribbit.core.RibbitError
import com.ribbit.core.UserId
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED

fun idpFailure() = RibbitError(UNAUTHORIZED, "user not authorized with identity provider")
fun userNotFound(id: UserId) = RibbitError(NOT_FOUND, "User $id not found")