package com.ribbit.posts.api

import com.ribbit.posts.PostData
import com.ribbit.ribbitJson
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class PostDataDtoV1(
    val title: String,
    val content: String
) {
    companion object {
        val lens = ribbitJson.autoBody<PostDataDtoV1>().toLens()
        val sample = PostDataDtoV1(
            title = "Frogs are Cool",
            content = "Super cool"
        )
    }
}

internal fun PostDataDtoV1.toModel() = PostData(
    title = title,
    content = content
)