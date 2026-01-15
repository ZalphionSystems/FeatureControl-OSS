package com.zalphion.featurecontrol.lib

import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map

fun <S: Any, F: Any> Result4k<S, F>.peekOrFail(fn: (S) -> Result4k<Any, F>) =
    flatMap { success -> fn(success).map { success } }

fun <S, I: Any, F: Any> Result4k<S, F>.failIfExists(
    test: (S) -> I?,
    toFail: (S, I) -> F
): Result4k<S, F> = flatMap { success -> test(success)?.let { toFail(success, it).asFailure() } ?: success.asSuccess() }