package com.ribbit.subs.api

import com.ribbit.subs.Sub
import com.ribbit.subs.SubId
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SubDtoV1(
    val id: SubId,
    val name: String
) {
    companion object {
        val lens = subsJson.autoBody<SubDtoV1>().toLens()
        val manyLens = subsJson.autoBody<Array<SubDtoV1>>().toLens()

        val sample = SubDtoV1(
            id = SubId.of("frogs"),
            name = "Frogs"
        )
    }
}

fun Sub.toDtoV1() = SubDtoV1(
    id = id,
    name = name
)