package com.zalphion.featurecontrol.jdbc

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory

// key-value pairs

internal fun <K: Value<*>> String.parseKeyValuePairs(
    kf: ValueFactory<K, *>
) = split(",")
    .filter { it.isNotEmpty() }
    .associate {
        val (key, value) = it.split("=")
        kf.parse(key) to value
    }

internal fun Map<out Any, Any>.toKeyValuePairs() =
    entries.joinToString(",") { (key, value) -> "$key=$value" }
