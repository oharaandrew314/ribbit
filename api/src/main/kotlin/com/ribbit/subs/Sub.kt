package com.ribbit.subs

import com.ribbit.users.Username
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.ValueFactory
import dev.forkhandles.values.and
import dev.forkhandles.values.maxLength
import dev.forkhandles.values.minLength
import se.ansman.kotshi.JsonSerializable

class SubId(value: String): StringValue(value) {
    companion object: ValueFactory<SubId, String>(
        ::SubId,
        validation = 3.minLength.and(20.maxLength),  // TODO extra validation
        parseFn = String::lowercase
    )
}

@JsonSerializable
data class Sub(
    val id: SubId,
    val name: String,
    val owner: Username
)