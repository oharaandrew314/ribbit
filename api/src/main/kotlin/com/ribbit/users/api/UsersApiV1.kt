package com.ribbit.users.api

import com.ribbit.RibbitErrorDto
import com.ribbit.posts.lens
import com.ribbit.toResponse
import com.ribbit.users.UserId
import com.ribbit.users.UserService
import dev.forkhandles.result4k.get
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import org.http4k.contract.ContractRoute
import org.http4k.contract.Tag
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.contract.security.Security
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.RequestContextLens

fun usersApiV1(service: UserService, auth: RequestContextLens<UserId>, bearerAuth: Security): List<ContractRoute> {
    val tag = Tag("users")

    val getUser = "users" / UserId.lens meta {
        operationId = "getUserV1"
        summary = "Get User"
        tags += tag

        returning(OK, UserDtoV1.lens to UserDtoV1.sample)
        returning(NOT_FOUND to "user not found")
    } bindContract GET to { userId ->
        {
            service
                .getUser(userId)
                .map { Response(OK).with(UserDtoV1.lens of it.toDtoV1()) }
                .mapFailure { it.toResponse() }
                .get()
        }
    }

    val updateProfile = "/users" meta {
        operationId = "updateUserProfileV1"
        summary = "Update User Profile"
        tags += tag
        security = bearerAuth

        receiving(UserDataDtoV1.lens to UserDataDtoV1.sample)
        returning(OK, UserDtoV1.lens to UserDtoV1.sample)
        returning(NOT_FOUND, RibbitErrorDto.lens to RibbitErrorDto.sample)
    } bindContract PUT to { request ->
        service
            .updateUser(auth(request), UserDataDtoV1.lens(request).toModel())
            .map { Response(OK).with(UserDtoV1.lens of it.toDtoV1()) }
            .mapFailure { it.toResponse() }
            .get()
    }

    return listOf(getUser, updateProfile)
}