package com.zalphion.featurecontrol.plugins

typealias Extensions = Map<String, String>

fun extensions(vararg items: Pair<String, String?>) = items.fold(emptyMap<String, String>()) { acc, (key, value) ->
    if (value == null) acc - key else acc + (key to value)
}

fun <T> Extensions.update(extension: Extension<T>, fn: (T?) -> T?): Extensions {
    return plus(extensions(
        extension of fn( extension(this))
    ))
}