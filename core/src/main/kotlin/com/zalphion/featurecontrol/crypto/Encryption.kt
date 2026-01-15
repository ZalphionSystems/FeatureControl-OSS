package com.zalphion.featurecontrol.crypto

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

interface Encryption {
    fun decrypt(cipherText: ByteArray): ByteArray?
    fun encrypt(plainText: ByteArray): ByteArray

    fun <V: StringValue> encrypt(plainText: V) = encrypt(plainText.value.encodeToByteArray())
    fun encrypt(plainText: String) = encrypt(plainText.encodeToByteArray())

    fun <V: StringValue> decrypt(cipherText: ByteArray, vf: StringValueFactory<V>) =
        decrypt(cipherText)?.let { vf.parse(it.decodeToString()) }

    fun decrypt(cipherText: Base64String) = decrypt(cipherText.decode())

    companion object
}

private const val AES_KEY_SIZE = 32

fun Encryption.Companion.aes(
    appSecret: AppSecret,
    keySalt: ByteArray?,
    algorithm: String,
    usage: String,
    ivLength: Int,
    random: Random,
    getSpec: (iv: ByteArray) -> AlgorithmParameterSpec
): Encryption = object: Encryption {
    private val key = MessageDigest.getInstance("SHA-256")
        .digest((keySalt ?: byteArrayOf()) + "$appSecret:$algorithm:$usage".encodeToByteArray())
        .copyOf(AES_KEY_SIZE)
        .let { SecretKeySpec(it, "AES") }

    override fun encrypt(plainText: ByteArray): ByteArray {
        val randomIv = random.nextBytes(ivLength)
        val cipherText = Cipher.getInstance(algorithm).apply {
            init(Cipher.ENCRYPT_MODE, key, getSpec(randomIv))
        }.doFinal(plainText)

        return randomIv + cipherText
    }

    override fun decrypt(cipherText: ByteArray): ByteArray? {
        val iv = cipherText.copyOfRange(0, ivLength)
        val encrypted = cipherText.copyOfRange(ivLength, cipherText.size)

        val cipher = Cipher.getInstance(algorithm).apply {
            init(Cipher.DECRYPT_MODE, key, getSpec(iv))
        }

        return try {
            cipher.doFinal(encrypted)
        } catch (_: GeneralSecurityException) {
            null
        }
    }
}

fun Encryption.Companion.aesGcm(
    appSecret: AppSecret,
    usage: String,
    random: Random,
    keySalt: ByteArray?,
    tagLength: Int = 128
) = aes(
    appSecret = appSecret,
    usage = usage,
    keySalt = keySalt,
    algorithm = "AES/GCM/NoPadding",
    ivLength = 12,
    random = random,
    getSpec = { GCMParameterSpec(tagLength, it) }
)
