package com.zalphion.featurecontrol

import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.Value
import dev.forkhandles.values.and
import dev.forkhandles.values.exactLength
import dev.forkhandles.values.maxLength
import dev.forkhandles.values.minLength
import dev.forkhandles.values.regex
import kotlin.random.Random

val keyValidation = 1.minLength
    .and(32.maxLength)
    .and("[a-zA-Z0-9-_]+".regex)

private val resourceIdChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
private const val length = 8

abstract class ResourceIdValueFactory<V: Value<String>>(coerceFn: (String) -> V): StringValueFactory<V>(
    fn = coerceFn,
    validation = length.exactLength.and { it.all(resourceIdChars::contains) }
) {
    fun random(random: Random) = of(List(length) { resourceIdChars.random(random) }.joinToString(""))
}