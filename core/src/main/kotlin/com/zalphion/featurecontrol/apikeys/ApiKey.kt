package com.zalphion.featurecontrol.apikeys

import dev.forkhandles.values.Maskers
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.and
import dev.forkhandles.values.exactLength
import dev.forkhandles.values.regex
import java.security.SecureRandom
import kotlin.random.asKotlinRandom

private val tokens = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('-', '_')
private const val size = 32

class ApiKey private constructor(value: String): StringValue(value, Maskers.hidden()) {
    companion object: StringValueFactory<ApiKey>(::ApiKey, size.exactLength.and("[a-zA-Z0-9-_]+".regex)) {
        private val random = SecureRandom().asKotlinRandom()
        fun secureRandom() = (1..size)
            .map { tokens.random(random) }
            .joinToString("")
            .let(::of)
    }
}