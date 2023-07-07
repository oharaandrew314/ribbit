package com.ribbit.subs.api

import com.ribbit.core.UserId
import com.ribbit.subs.SubId
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.MapAdapter
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.KotshiJsonAdapterFactory

@KotshiJsonAdapterFactory
private object SubsJsonAdapterFactory : JsonAdapter.Factory by KotshiSubsJsonAdapterFactory

val subsJson = ConfigurableMoshi(
    Moshi.Builder()
        .add(SubsJsonAdapterFactory)
        .add(ListAdapter)
        .add(MapAdapter)
        .asConfigurable()
        .withStandardMappings()
        .value(UserId)
        .value(SubId)
        .done()
)
