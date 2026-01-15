package com.zalphion.featurecontrol.lib

data class Update<T>(val value: T)

/** Helper that understands all 3 possible states:
 * 1. Update is null -> use default
 * 2. Update supports and has null value -> use null
 * 3. Update has value -> use value
 */
fun <T> Update<T>?.effective(default: T) = if (this == null) default else value