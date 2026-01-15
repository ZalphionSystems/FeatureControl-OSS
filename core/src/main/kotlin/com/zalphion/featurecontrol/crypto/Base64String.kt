package com.zalphion.featurecontrol.crypto

import dev.forkhandles.values.Base64StringValueFactory
import dev.forkhandles.values.StringValue
import kotlin.io.encoding.Base64

class Base64String private constructor(value: String): StringValue(value) {
    companion object: Base64StringValueFactory<Base64String>(::Base64String) {
        private val base64 = Base64.Mime
    }

    fun decode() = base64.decode(value)
}