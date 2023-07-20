package com.ribbit.core

data class Cursor<Item: Any, Id: Any>(
    val items: List<Item>,
    val next: Id?,
    private val getPage: (Id) -> Cursor<Item, Id>?
) {
    fun all(): Sequence<Item> = sequence {
        yieldAll(items)
        val nextCursor = next?.let(getPage)
        if (nextCursor != null) {
            yieldAll(nextCursor.all())
        }
    }
}