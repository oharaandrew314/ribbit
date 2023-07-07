package com.ribbit.core

import dev.forkhandles.values.NonEmptyStringValueFactory
import dev.forkhandles.values.StringValue

class UserId(value: String): StringValue(value) {
    companion object: NonEmptyStringValueFactory<UserId>(::UserId)
}

data class User(
    val id: UserId,
    val name: String
)