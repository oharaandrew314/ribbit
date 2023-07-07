package com.ribbit.core

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue

class AccessToken private constructor(value: String): StringValue(value) {
    companion object: NonBlankStringValueFactory<AccessToken>(::AccessToken)
}