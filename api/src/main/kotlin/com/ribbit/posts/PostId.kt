package com.ribbit.posts

import com.github.ksuid.Ksuid
import dev.forkhandles.values.AbstractComparableValue
import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.value
import java.time.Instant

class PostId(value: Ksuid): AbstractComparableValue<PostId, Ksuid>(value) {
    companion object: ValueFactory<PostId, Ksuid>(::PostId, parseFn = Ksuid::fromString)

    val time: Instant get() = value.instant
}

// dynamo db attribute converter
fun <V: Value<Ksuid>> Attribute.Companion.value(vf: ValueFactory<V, Ksuid>) = string()
    .map(Ksuid::fromString, Ksuid::toString)
    .value(vf)