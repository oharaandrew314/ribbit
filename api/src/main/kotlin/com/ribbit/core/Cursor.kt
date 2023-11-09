package com.ribbit.core

data class Cursor<Item: Any, Id: Any>(
    val items: List<Item>,
    val next: Id?
)