package dev.aohara.ribbit

import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_ACCESS_KEY_ID
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.AWS_SECRET_ACCESS_KEY
import org.http4k.connect.amazon.core.model.AccessKeyId
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.SecretAccessKey

val baseTestEnv = Environment.defaults(
    AWS_REGION of Region.CA_CENTRAL_1,
    AWS_ACCESS_KEY_ID of AccessKeyId.of("fake-id"),
    AWS_SECRET_ACCESS_KEY of SecretAccessKey.of("fake-value")
)