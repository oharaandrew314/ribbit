package com.ribbit

import com.ribbit.posts.api.postsApiV1
import com.ribbit.posts.postService
import com.ribbit.subs.api.subsApiV1
import com.ribbit.subs.subService
import com.ribbit.users.api.usersApiV1
import com.ribbit.users.userService
import io.andrewohara.utils.http4k.logErrors
import io.andrewohara.utils.http4k.logSummary
import org.http4k.client.Java8HttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.openapi.v3.OpenApi3ApiRenderer
import org.http4k.contract.ui.swaggerUiLite
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Method.POST
import org.http4k.core.Method.DELETE
import org.http4k.core.RequestContexts
import org.http4k.core.then
import org.http4k.filter.AllowAll
import org.http4k.filter.AnyOf
import org.http4k.filter.CorsPolicy
import org.http4k.filter.OriginPolicy
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Moshi
import org.http4k.lens.csv
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.serverless.ApiGatewayV2LambdaFunction
import org.http4k.serverless.AppLoader
import java.time.Clock

private val corsOriginsKey = EnvironmentKey.csv().optional("cors_origins")

fun createApi(env: Environment): HttpHandler {
    val contexts = RequestContexts()
    val clock = Clock.systemUTC()
    val internet = ResponseFilters.logSummary()
        .then(Java8HttpClient())

    val corsPolicy = CorsPolicy(
        originPolicy = corsOriginsKey(env)
            ?.let { OriginPolicy.AnyOf(it) }
            ?: OriginPolicy.AllowAll(),
        headers = listOf("Authorization"),
        methods = listOf(GET, POST, PUT, DELETE),
    )

    val api = contract {
        val users = userService(env, clock, aws = internet)
        val subs = subService(env)
        val posts = postService(env, clock, users, subs)

        routes += usersApiV1(users)
        routes += subsApiV1(subs)
        routes += postsApiV1(posts)

        renderer = OpenApi3(
            apiInfo = ApiInfo("Ribbit Api", "1"),
            apiRenderer = OpenApi3ApiRenderer(Moshi),
            json = Moshi
        )

        descriptionPath = "openapi.json"
    }

    val ui = swaggerUiLite {
        pageTitle = "Ribbit API"
        url = "openapi.json"
        requestSnippetsEnabled = true
        deepLinking = true
        displayOperationId = true
    }

    return ServerFilters.InitialiseRequestContext(contexts)
        .then(ServerFilters.Cors(corsPolicy))
        .then(ResponseFilters.logSummary())
        .then(ServerFilters.logErrors())
        .then(routes(api, ui))
}

fun main() {
    val env = Environment.ENV

    createApi(env)
        .asServer(SunHttp(8080))
        .start()
        .also { println("Running on http://localhost:${it.port()}") }
        .block()
}

class Http4kLambdaHandler : ApiGatewayV2LambdaFunction(AppLoader {
    createApi(Environment.from(it))
})
