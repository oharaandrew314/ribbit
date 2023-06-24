package com.ribbit.users

import dev.forkhandles.values.NonEmptyStringValueFactory
import dev.forkhandles.values.StringValue

class UserId(value: String): StringValue(value) {
    companion object: NonEmptyStringValueFactory<UserId>(::UserId)
}