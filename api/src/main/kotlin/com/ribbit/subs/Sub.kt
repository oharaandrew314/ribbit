package com.ribbit.subs

import com.ribbit.users.UserId
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.ValueFactory
import dev.forkhandles.values.and
import dev.forkhandles.values.maxLength
import dev.forkhandles.values.minLength

class SubId(value: String): StringValue(value) {
    companion object: ValueFactory<SubId, String>(
        ::SubId,
        validation = 3.minLength.and(20.maxLength),  // TODO extra validation
        parseFn = String::lowercase
    )
}

data class Sub(
    val id: SubId,
    val name: String,
    val owner: UserId
)