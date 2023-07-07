package com.ribbit.subs.api

import com.ribbit.subs.SubData
import com.ribbit.subs.SubId
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SubDataDtoV1(
    val id: SubId,
    val name: String
) {
    companion object {
        val lens = subsJson.autoBody<SubDataDtoV1>().toLens()

        val sample = SubDataDtoV1(
            id = SubId.of("frogs"),
            name = "Frogs"
        )
    }
}

internal fun SubDataDtoV1.toModel() = SubData(
    id = id,
    name = name
)