package com.ribbit.subs.api

import com.ribbit.core.Cursor
import com.ribbit.core.CursorDtoV1
import com.ribbit.ribbitJson
import com.ribbit.subs.Sub
import com.ribbit.subs.SubId
import com.ribbit.users.Username
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SubDtoV1(
    val id: SubId,
    val name: String,
    val owner: Username
) {
    companion object {
        val lens = ribbitJson.autoBody<SubDtoV1>().toLens()

        val sample = SubDtoV1(
            id = SubId.of("frogs"),
            name = "Frogs",
            owner = Username.of("user1")
        )
    }
}

@JsonSerializable
data class SubCursorDtoV1(
    override val items: List<SubDtoV1>,
    override val next: String?
): CursorDtoV1<SubDtoV1> {
    companion object {
        val lens = ribbitJson.autoBody<SubCursorDtoV1>().toLens()
        val sample = SubCursorDtoV1(
            items = listOf(SubDtoV1.sample),
            next = "nextSub"
        )
    }
}

fun Sub.toDtoV1() = SubDtoV1(
    id = id,
    name = name,
    owner = owner
)

fun Cursor<Sub, SubId>.toDtoV1() = SubCursorDtoV1(
    items = items.map(Sub::toDtoV1),
    next = next?.value
)