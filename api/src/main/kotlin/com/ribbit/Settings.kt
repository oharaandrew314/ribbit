package com.ribbit

import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.connect.amazon.core.model.KMSKeyId
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.lens.csv
import org.http4k.lens.int
import org.http4k.lens.value

object Settings {
    val postsTableName = EnvironmentKey.value(TableName).required("POSTS_TABLE_NAME")
    val postsPageSize = EnvironmentKey.int().defaulted("POSTS_PAGE_SIZE", 100)
    val subsTableName = EnvironmentKey.value(TableName).required("SUBS_TABLE_NAME")
    val usersTableName = EnvironmentKey.value(TableName).required("USERS_TABLE_NAME")
    val googleAudience = EnvironmentKey.required("GOOGLE_AUDIENCE")
    val corsOrigins = EnvironmentKey.csv().required("CORS_ORIGINS")
    val tokensKeyId = EnvironmentKey.value(KMSKeyId).required("TOKENS_KEY_ID")
    val jwtIssuer = EnvironmentKey.required("JWT_ISSUER")
}