package com.ribbit.users.api

import com.ribbit.users.UserId
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.MapAdapter
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.KotshiJsonAdapterFactory
import com.ribbit.users.api.KotshiUsersJsonAdapterFactory

@KotshiJsonAdapterFactory
private object UsersJsonAdapterFactory : JsonAdapter.Factory by KotshiUsersJsonAdapterFactory

val usersJson = ConfigurableMoshi(
    Moshi.Builder()
        .add(UsersJsonAdapterFactory)
        .add(ListAdapter)
        .add(MapAdapter)
        .asConfigurable()
        .withStandardMappings()
        .value(UserId)
        .done()
)
