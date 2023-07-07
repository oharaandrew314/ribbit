package dev.aohara.ribbit

import com.ribbit.core.AccessToken
import org.http4k.core.Request

fun Request.withToken(token: AccessToken) = header("Authorization", "Bearer ${token.value}")