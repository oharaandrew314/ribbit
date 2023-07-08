package com.ribbit.core

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import dev.aohara.nimbuskms.DeterministicJwtClaimSetVerifier
import dev.aohara.nimbuskms.KmsJwsKeySelector
import dev.aohara.nimbuskms.KmsJwsSigner
import dev.aohara.nimbuskms.KmsJwsVerifierFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.connect.amazon.core.model.KMSKeyId
import org.http4k.connect.amazon.kms.Http
import org.http4k.connect.amazon.kms.KMS
import org.http4k.core.HttpHandler
import org.http4k.lens.value
import java.time.Clock
import java.time.Duration
import java.util.Date

class KmsJwtAuthorizer(
    private val clock: Clock,
    kms: KMS,
    keyId: KMSKeyId,
    private val audience: List<String>,
    private val issuer: String,
    private val duration: Duration
): Authorizer, Issuer {

    private val logger = KotlinLogging.logger {}

    private val signer = KmsJwsSigner(kms, keyId)
    private val processor = DefaultJWTProcessor<SecurityContext>().apply {
        jwtClaimsSetVerifier = DeterministicJwtClaimSetVerifier(
            clock = clock,
            exactMatchClaims = JWTClaimsSet.Builder()
                .issuer(issuer)
                .audience(audience)
                .build(),
            requiredClaims = setOf("name")
        )
        jwsKeySelector = KmsJwsKeySelector<SecurityContext>(keyId)
        jwsVerifierFactory = KmsJwsVerifierFactory(kms)
//        jwsKeySelector = KmsPublicKeyJwsKeySelector(kms, keyId)  FIXME
    }

    override fun authorize(token: AccessToken) =
        kotlin.runCatching {
            SignedJWT.parse(token.value).let { processor.process(it, null) }
        }
        .onFailure { logger.debug { "Failed to process JWT: $it" } }
        .map {
            User(
                id = UserId.parse(it.subject),
                name = it.claims.getValue("name").toString()
            )
        }
        .getOrNull()

    override fun issue(principal: User): AccessToken {
        val issuedAt = clock.instant()

        val claims = JWTClaimsSet.Builder()
            .subject(principal.id.value)
            .audience(audience)
            .issuer(issuer)
            .issueTime(Date.from(issuedAt))
            .expirationTime(Date.from(issuedAt + duration))
            .claim("name", principal.name)
            .build()

        val header = JWSHeader.Builder(JWSAlgorithm.RS512)
            .build()

        return SignedJWT(header, claims)
            .apply { sign(signer) }
            .serialize()
            .let { AccessToken.of(it) }
    }
}

val tokensKeyIdKey = EnvironmentKey.value(KMSKeyId).required("TOKENS_KEY_ID")
val issuerKey = EnvironmentKey.required("ISSUER")

fun createAuthorizer(env: Environment, clock: Clock, internet: HttpHandler): KmsJwtAuthorizer {
    return KmsJwtAuthorizer(
        clock = clock,
        kms = KMS.Http(env, http = internet),
        keyId = tokensKeyIdKey(env),
        audience = listOf(issuerKey(env)),
        duration = Duration.ofHours(1),
        issuer = issuerKey(env)
    )
}