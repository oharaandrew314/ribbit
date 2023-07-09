package com.ribbit.auth

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.JWKSourceBuilder
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import com.ribbit.users.User
import com.ribbit.users.UserId
import dev.aohara.nimbuskms.DeterministicJwtClaimSetVerifier
import io.github.oshai.kotlinlogging.KotlinLogging
import org.http4k.core.Uri
import java.net.URL
import java.time.Clock

private val googleJwkUri = Uri.of("https://www.googleapis.com/oauth2/v3/certs")
private const val googleIss = "accounts.google.com"

class GoogleAuthorizer(
    audience: List<String>,
    clock: Clock,
    issuer: String = googleIss,
    algorithm: JWSAlgorithm = JWSAlgorithm.RS256,
    jwkUri: Uri = googleJwkUri
): Authorizer {

    private val logger = KotlinLogging.logger {}

    private val processor = DefaultJWTProcessor<SecurityContext>().apply {
        jwtClaimsSetVerifier = DeterministicJwtClaimSetVerifier(
            exactMatchClaims = JWTClaimsSet.Builder()
                .issuer(issuer)
                .audience(audience)
                .build(),
            requiredClaims = setOf("name"),
            clock = clock
        )
        jwsKeySelector = JWSVerificationKeySelector(
            algorithm,
            JWKSourceBuilder.create<SecurityContext>(URL(jwkUri.toString())).cache(true).build()
        )
    }

    override fun authorize(token: AccessToken) = kotlin
        .runCatching { SignedJWT.parse(token.value).let { processor.process(it, null) } }
        .onFailure { logger.debug {"Failed to process JWT: $it" } }
        .map {
            User(
                id = UserId.parse(it.subject),
                name = it.claims.getValue("name").toString()
            )
        }
        .getOrNull()
}