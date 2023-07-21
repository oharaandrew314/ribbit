package com.ribbit

import com.github.ksuid.KsuidGenerator
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.RemoteJWKSet
import com.nimbusds.jose.proc.JWSKeySelector
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.ribbit.auth.Authorizer
import com.ribbit.auth.jwtAuthorizer
import com.ribbit.posts.PostRepo
import com.ribbit.posts.PostService
import com.ribbit.posts.api.postsApiV1
import com.ribbit.posts.postsTable
import com.ribbit.subs.SubRepo
import com.ribbit.subs.SubService
import com.ribbit.subs.api.subsApiV1
import com.ribbit.subs.subsTable
import com.ribbit.users.EmailHash
import com.ribbit.users.UserRepo
import com.ribbit.users.UserService
import com.ribbit.users.api.usersApiV1
import com.ribbit.users.usersTable
import io.andrewohara.utils.http4k.logErrors
import io.andrewohara.utils.http4k.logSummary
import org.http4k.client.Java8HttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.CredentialsChain
import org.http4k.connect.amazon.Environment
import org.http4k.connect.amazon.Profile
import org.http4k.connect.amazon.RegionProvider
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.Http
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.openapi.v3.OpenApi3ApiRenderer
import org.http4k.contract.security.BearerAuthSecurity
import org.http4k.contract.ui.swaggerUiLite
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Method.POST
import org.http4k.core.Method.DELETE
import org.http4k.core.RequestContexts
import org.http4k.core.then
import org.http4k.filter.AnyOf
import org.http4k.filter.CorsPolicy
import org.http4k.filter.OriginPolicy
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Moshi
import org.http4k.lens.RequestContextKey
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.serverless.ApiGatewayV2LambdaFunction
import org.http4k.serverless.AppLoader
import java.net.URL
import java.time.Clock
import java.util.Random

class RibbitService(
    val authorizer: Authorizer,
    val posts: PostService,
    val users: UserService,
    val subs: SubService
)

fun ribbitService(
    env: Environment,
    clock: Clock = Clock.systemUTC(),
    internet: HttpHandler = ResponseFilters.logSummary().then(Java8HttpClient()),
    keySelector: JWSKeySelector<SecurityContext>? = null,
): RibbitService {
    val dynamo = DynamoDb.Http(
        credentialsProvider = (CredentialsChain.Environment(env) orElse CredentialsChain.Profile(env)).provider(),
        region = (RegionProvider.Environment(env) orElse RegionProvider.Profile(env)).orElseThrow(),
        http = internet
    )
    val jwkUri = env[Settings.jwtIssuer].path("/.well-known/jwks.json")

    val userRepo = UserRepo(dynamo.usersTable(env[Settings.usersTableName]))
    val postsRepo = PostRepo(dynamo.postsTable(env[Settings.postsTableName]))
    val subsRepo = SubRepo(dynamo.subsTable(env[Settings.subsTableName]))

    val authorizer = jwtAuthorizer(
        clock = clock,
        audience = env[Settings.jwtAudiences],
        issuer = env[Settings.jwtIssuer].toString(),
        keySelector = keySelector ?: JWSVerificationKeySelector(
            JWSAlgorithm.RS256,
            RemoteJWKSet(URL(jwkUri.toString()))
        )
    )
    return RibbitService(
        authorizer = authorizer,
        users = UserService(userRepo),
        posts = PostService(
            postsRepo, subsRepo, userRepo,
            clock = clock,
            ksuidGen = KsuidGenerator(Random(env[Settings.randomSeed]))
        ),
        subs = SubService(subsRepo, userRepo)
    )
}

fun RibbitService.toApi(env: Environment): HttpHandler {
    val corsPolicy = CorsPolicy(
        originPolicy = OriginPolicy.AnyOf(env[Settings.corsOrigins]),
        headers = listOf("Authorization"),
        methods = listOf(GET, POST, PUT, DELETE),
    )

    val contexts = RequestContexts()
    val authLens = RequestContextKey.required<EmailHash>(contexts, "ribbit-auth")

    val security = BearerAuthSecurity(authLens, authorizer)

    val api = contract {
        routes += usersApiV1(users, authLens, security)
        routes += subsApiV1(subs, authLens, security)
        routes += postsApiV1(posts, authLens, security)

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

    ribbitService(env)
        .toApi(env)
        .asServer(SunHttp(8080))
        .start()
        .also { println("Running on http://localhost:${it.port()}") }
        .block()
}

class Http4kLambdaHandler : ApiGatewayV2LambdaFunction(AppLoader { envMap ->
    val env = Environment.from(envMap)
    ribbitService(env).toApi(env)
})
