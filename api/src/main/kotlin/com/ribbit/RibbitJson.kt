package com.ribbit

import com.ribbit.posts.PostId
import com.ribbit.subs.SubId
import com.ribbit.users.EmailHash
import com.ribbit.users.Username
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
private object RibbitJsonAdapterFactory : JsonAdapter.Factory by KotshiRibbitJsonAdapterFactory

val ribbitJson = ConfigurableMoshi(
    Moshi.Builder()
        .add(RibbitJsonAdapterFactory)
        .add(ListAdapter)
        .add(MapAdapter)
        .asConfigurable()
        .withStandardMappings()
        .value(SubId)
        .value(PostId)
        .value(Username)
        .value(EmailHash)
        .done()
)
