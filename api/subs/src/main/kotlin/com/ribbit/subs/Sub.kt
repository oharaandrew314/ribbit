package com.ribbit.subs

import dev.forkhandles.values.Base36StringValueFactory
import dev.forkhandles.values.NonEmptyStringValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.exactLength
import io.andrewohara.utils.IdGenerator

class SubId(value: String): StringValue(value) {
    companion object: Base36StringValueFactory<SubId>(
        ::SubId,
        validation = 8.exactLength,
        parseFn = String::uppercase
    ) {
        fun next() = SubId.parse(IdGenerator.nextBase36(8))
    }
}

class SubName(value: String): StringValue(value) {
    companion object: NonEmptyStringValueFactory<SubName>(::SubName) // TODO validation
}

data class Sub(
    val id: SubId,
    val name: SubName
)