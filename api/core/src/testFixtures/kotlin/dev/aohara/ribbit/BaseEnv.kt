package dev.aohara.ribbit

import com.ribbit.core.issuerKey
import com.ribbit.core.tokensKeyIdKey
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_ACCESS_KEY_ID
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.AWS_SECRET_ACCESS_KEY
import org.http4k.connect.amazon.core.model.AccessKeyId
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.SecretAccessKey
import org.http4k.connect.amazon.kms.KMS
import org.http4k.connect.amazon.kms.createKey
import org.http4k.connect.amazon.kms.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.kms.model.KeyUsage
import org.http4k.core.with
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

val baseTestEnv = Environment.defaults(
    AWS_REGION of Region.CA_CENTRAL_1,
    AWS_ACCESS_KEY_ID of AccessKeyId.of("fake-id"),
    AWS_SECRET_ACCESS_KEY of SecretAccessKey.of("fake-value")
)

fun Environment.withAuthorizer(kms: KMS) = with(
    tokensKeyIdKey of kms
        .createKey(CustomerMasterKeySpec.RSA_2048, KeyUsage.SIGN_VERIFY)
        .valueOrThrow()
        .KeyMetadata.KeyId,
    issuerKey of "ribbit-test"
)

val clock: Clock = Clock.fixed(Instant.parse("2023-07-06T12:00:00Z"), ZoneOffset.UTC)