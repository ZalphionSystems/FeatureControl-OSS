package com.zalphion.featurecontrol.crypto

import dev.forkhandles.values.StringValue
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun interface Signing {
    operator fun invoke(input: String): Base64String
    operator fun invoke(input: StringValue) = invoke(input.value)

    companion object
}

private const val ALGORITHM = "HmacSHA256"

fun Signing.Companion.hMacSha256(appSecret: AppSecret): Signing {
    val key = MessageDigest.getInstance("SHA-256")
        .digest("signing:$appSecret:$ALGORITHM".encodeToByteArray())
        .let { SecretKeySpec(it, ALGORITHM) }

    return Signing { input ->
        Mac.getInstance(ALGORITHM).run {
            init(key)
            doFinal(input.encodeToByteArray())
        }.let(Base64String::encode)
    }
}