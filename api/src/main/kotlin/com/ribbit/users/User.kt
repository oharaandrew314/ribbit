package com.ribbit.users

import dev.forkhandles.values.NonEmptyStringValueFactory
import dev.forkhandles.values.StringValue
import se.ansman.kotshi.JsonSerializable
import java.security.MessageDigest
import java.util.Base64

class EmailHash private constructor(value: String): StringValue(value) {
    companion object: NonEmptyStringValueFactory<EmailHash>(::EmailHash) {
        private val encoder = Base64.getUrlEncoder()

        fun fromEmail(email: String) = MessageDigest
            .getInstance("SHA-1")
            .digest(email.encodeToByteArray())
            .let { encoder.encodeToString(it) }
            .replace("=", "")
            .replace("-", "")
            .let(EmailHash::of)
    }
}

class Username private constructor(value: String): StringValue(value) { // TODO must be url-safe
    companion object: NonEmptyStringValueFactory<Username>(::Username)
}

@JsonSerializable
data class User(
    val emailHash: EmailHash,
    val name: Username
)

data class UserData(
    val name: Username
)