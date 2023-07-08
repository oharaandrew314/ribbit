package dev.aohara.ribbit

import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.onFailure

fun <R: Any, E: Any> Result4k<R, E>.valueOrThrow(): R = onFailure { error(it) }