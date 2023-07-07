package com.ribbit.posts.api

import com.ribbit.core.UserId
import com.ribbit.posts.PostId
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
private object PostsJsonAdapterFactory : JsonAdapter.Factory by KotshiPostsJsonAdapterFactory

val postsJson = ConfigurableMoshi(
    Moshi.Builder()
        .add(PostsJsonAdapterFactory)
        .add(ListAdapter)
        .add(MapAdapter)
        .asConfigurable()
        .withStandardMappings()
        .value(UserId)
        .value(SubId)
        .value(PostId)
        .done()
)
