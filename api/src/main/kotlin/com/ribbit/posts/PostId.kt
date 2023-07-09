package com.ribbit.posts

import dev.forkhandles.values.Base36StringValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.exactLength
import io.andrewohara.utils.IdGenerator

class PostId(value: String): StringValue(value) {
    companion object: Base36StringValueFactory<PostId>(
        ::PostId,
        validation = 8.exactLength,
        parseFn = String::uppercase
    ) {
        fun next() = PostId.parse(IdGenerator.nextBase36(8))
    }
}