package com.zalphion.featurecontrol.crypto

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.random.Random

class EncryptionTest {

    private val appSecret = AppSecret.parse("password")
    private val random = Random(1337)

    @Test
    fun encrypt() {
        Encryption
            .aesGcm(appSecret, "test", random, "salt".encodeToByteArray())
            .encrypt("secret stuff")
            .let(Base64String::encode)
            .shouldBe(Base64String.parse("G97I40wnQJwmiy5cnvFr9y1rGU/uRg6x+fUcCVAnVCzzhOTNBqAZGw=="))
    }

    @Test
    fun `decrypt - success`() {
        val crypt = Encryption.aesGcm(appSecret, "test", random, "salt".encodeToByteArray())
        Base64String.parse("G97I40wnQJwmiy5cnvFr9y1rGU/uRg6x+fUcCVAnVCzzhOTNBqAZGw==")
            .let(crypt::decrypt)
            .shouldNotBeNull()
            .decodeToString()
            .shouldBe("secret stuff")
    }

    @Test
    fun `decrypt - malformed`() {
        val crypt = Encryption.aesGcm(appSecret, "test", random, "salt".encodeToByteArray())
        "this isn't encrypted at all!"
            .encodeToByteArray()
            .let(crypt::decrypt)
            .shouldBeNull()
    }

    @Test
    fun `decrypt - wrong key`() {
        val encrypted = Encryption
            .aesGcm(appSecret, "test", random, "salt".encodeToByteArray())
            .encrypt("secret stuff")

        Encryption
            .aesGcm(appSecret, "test2", random, "salt".encodeToByteArray())
            .decrypt(encrypted)
            .shouldBeNull()
    }

    @Test
    fun `crypt decrypt`() {
        val crypt = Encryption.aesGcm(appSecret, "test", random, "salt".encodeToByteArray())
        val encrypted = crypt.encrypt("secret stuff")
        crypt.decrypt(encrypted)
            .shouldNotBeNull()
            .decodeToString() shouldBe "secret stuff"
    }
}