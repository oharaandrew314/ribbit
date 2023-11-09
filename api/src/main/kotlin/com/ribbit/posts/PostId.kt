package com.ribbit.posts

import com.github.ksuid.Ksuid
import dev.forkhandles.values.AbstractComparableValue
import dev.forkhandles.values.ValueFactory
import java.time.Instant

class PostId(value: Ksuid): AbstractComparableValue<PostId, Ksuid>(value) {
    companion object: ValueFactory<PostId, Ksuid>(::PostId, parseFn = Ksuid::fromString)

    val time: Instant get() = value.instant
}