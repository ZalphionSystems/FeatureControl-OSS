package com.zalphion.featurecontrol.web

fun cssStyle(vararg styles: Pair<String, String>) =
    styles.joinToString(";") { (key, value) -> "$key: $value" }