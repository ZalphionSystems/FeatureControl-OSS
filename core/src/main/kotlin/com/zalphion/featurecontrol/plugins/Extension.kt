package com.zalphion.featurecontrol.plugins

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory

class Extension<T>(
    val key: String,
    private val parse: (String) -> T,
    private val show: (T) -> String
) {
    operator fun invoke(extensions: Extensions) = extensions[key]?.let(parse)

    infix fun of(value: T?) = key to value?.let(show)

    companion object {
        inline fun <reified T: StringValue> of(
            key: String,
            factory: StringValueFactory<T>
        ): Extension<T> {
            return Extension(key, factory::parse, factory::show)
        }
    }
}