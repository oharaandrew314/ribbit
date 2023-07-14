package com.ribbit.core

interface CursorDtoV1<Item: Any> {
    val items: List<Item>
    val next: String?
}