package com.ribbit.users

import dev.forkhandles.values.NonEmptyStringValueFactory
import dev.forkhandles.values.StringValue
import java.security.MessageDigest
import java.util.Base64

class UserId(value: String): StringValue(value) {
    companion object: NonEmptyStringValueFactory<UserId>(::UserId) {
        private val encoder = Base64.getUrlEncoder()

        fun fromEmail(email: String) = MessageDigest
            .getInstance("SHA-1")
            .digest(email.encodeToByteArray())
            .let { encoder.encodeToString(it) }
            .replace("=", "")
            .replace("-", "")
            .let(UserId::of)
    }
}

data class User(
    val id: UserId,
    val name: String
)