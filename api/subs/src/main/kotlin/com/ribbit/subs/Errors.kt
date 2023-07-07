package com.ribbit.subs

import com.ribbit.core.RibbitError
import org.http4k.core.Status

fun subNotFound(id: SubId) = RibbitError(Status.NOT_FOUND, "Sub $id not found")
fun duplicateSub(id: SubId) = RibbitError(Status.BAD_REQUEST, "Duplicate sub: $id")