package com.ribbit.users

import com.ribbit.core.AccessToken
import com.ribbit.core.Authorizer
import com.ribbit.core.Issuer
import com.ribbit.core.RibbitError
import com.ribbit.core.User
import com.ribbit.core.UserId
import com.ribbit.core.createAuthorizer
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asResultOr
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.Http
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.core.HttpHandler
import org.http4k.lens.value
import java.time.Clock

class UserService internal constructor(
    internal val users: UserRepo,
    internal val issuer: Issuer,
    private val googleAuth: Authorizer
) {

    fun loginWithGoogle(googleIdToken: AccessToken): Result4k<AccessToken, RibbitError> {
        val principal = googleAuth.authorize(googleIdToken) ?: return Failure(idpFailure())
        users += principal

        return Success(issuer.issue(principal))
    }

    fun getUser(id: UserId): Result4k<User, RibbitError> {
        return users[id].asResultOr { userNotFound(id) }
    }
}

internal val usersTableNameKey = EnvironmentKey.value(TableName).required("USERS_TABLE_NAME")
internal val googleAudienceKey = EnvironmentKey.required("GOOGLE_AUDIENCE")

fun userService(env: Environment, clock: Clock, internet: HttpHandler): UserService {
    val dynamo = DynamoDb.Http(env, http = internet)
    val issuer = createAuthorizer(env, clock, internet = internet)

    val googleAuth = GoogleAuthorizer(
        audience = listOf(googleAudienceKey(env)),
        clock = clock
    )

    val repo = UserRepo(dynamo.usersTable(env[usersTableNameKey]))
    return UserService(repo, issuer, googleAuth)
}