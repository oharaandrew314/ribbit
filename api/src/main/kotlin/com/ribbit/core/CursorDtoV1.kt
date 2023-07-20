package com.ribbit.core

import org.http4k.lens.Query
import org.http4k.lens.int

interface CursorDtoV1<Item: Any> {
    val items: List<Item>
    val next: String?

    companion object {
        val limitLens = Query.int()
            .map(nextIn = { it.coerceIn(1, 100) }, nextOut = { it })
            .defaulted("limit", 100)
    }
}