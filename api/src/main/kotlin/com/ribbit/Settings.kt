package com.ribbit

import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.lens.csv
import org.http4k.lens.int
import org.http4k.lens.long
import org.http4k.lens.uri
import org.http4k.lens.value

object Settings {
    val postsTableName = EnvironmentKey.value(TableName).required("POSTS_TABLE_NAME")
    val pageSize = EnvironmentKey.int()
        .map(Int::toUInt, UInt::toInt)
        .defaulted("PAGE_SIZE", 100u)
    val subsTableName = EnvironmentKey.value(TableName).required("SUBS_TABLE_NAME")
    val usersTableName = EnvironmentKey.value(TableName).required("USERS_TABLE_NAME")
    val corsOrigins = EnvironmentKey.csv().required("CORS_ORIGINS")
    val jwtIssuer = EnvironmentKey.uri().required("JWT_ISSUER")
    val jwtAudiences = EnvironmentKey.csv().required("JWT_AUDIENCES")
    val randomSeed = EnvironmentKey.long().optional("RANDOM_SEED")
}