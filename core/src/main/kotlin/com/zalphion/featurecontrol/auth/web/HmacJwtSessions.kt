package com.zalphion.featurecontrol.auth.web

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.proc.JWSKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import com.zalphion.featurecontrol.crypto.AppSecret
import com.zalphion.featurecontrol.users.UserId
import io.github.oshai.kotlinlogging.KotlinLogging
import java.security.MessageDigest
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

// allow for some clock skew
private val notBeforeGracePeriod = Duration.ofMinutes(1)
private val jwsAlgorithm = JWSAlgorithm.HS256

private const val javaCryptoAlg = "HmacSHA256"
private const val NONCE_LENGTH = 32 // balance entropy with cookie size
private const val NONCE_PARAM = "nonce"

fun Sessions.Companion.hMacJwt(
    clock: Clock,
    appSecret: AppSecret,
    issuer: String,
    sessionLength: Duration,
    random: Random
) = object: Sessions {
    private val logger = KotlinLogging.logger {  }
    private val base64Encoder = Base64.getUrlEncoder().withoutPadding()
    private val base64Decoder = Base64.getUrlDecoder()

    val processor = DefaultJWTProcessor<SecurityContext>().apply {
        jwtClaimsSetVerifier = object : DefaultJWTClaimsVerifier<SecurityContext>(
            JWTClaimsSet.Builder().issuer(issuer).audience(issuer).build(),
            emptySet()
        ) {
            override fun currentTime() = Date.from(clock.instant())
        }
        jwsKeySelector = JWSKeySelector { header, _ ->
            val nonceBase64 = header.getCustomParam(NONCE_PARAM) as? String
            val nonce = nonceBase64?.let(base64Decoder::decode) ?: error("Missing $NONCE_PARAM")
            listOf(deriveKey(nonce))
        }
    }

    /**
     * Redistribute (but not improve) entropy of a potentially weak secret
     * Add nonce to reduce the surface area of a compromised key
     */
    private fun deriveKey(nonce: ByteArray): SecretKey {
        return MessageDigest.getInstance("SHA-256")
            .digest(appSecret.value.encodeToByteArray() + nonce + "jwt:session".encodeToByteArray())
            .let { SecretKeySpec(it, javaCryptoAlg) }
    }

    override fun create(userId: UserId): Pair<String, Instant> {
        val nonce = random.nextBytes(ByteArray(NONCE_LENGTH))

        val claims = JWTClaimsSet.Builder()
            .subject(userId.value)
            .issuer(issuer)
            .audience(issuer)
            .issueTime(Date.from(clock.instant()))
            .notBeforeTime(Date.from(clock.instant() - notBeforeGracePeriod))
            .expirationTime(Date.from(clock.instant() + sessionLength))
            .build()

        // Create signed JWT
        val header = JWSHeader.Builder(jwsAlgorithm)
            .customParam(NONCE_PARAM, base64Encoder.encodeToString(nonce))
            .build()

        val signed = SignedJWT(header, claims).apply {
            sign(MACSigner(deriveKey(nonce)))
        }

        return signed.serialize() to claims.expirationTime.toInstant()
    }

    override fun verify(sessionId: String): UserId? {
        val jwt = SignedJWT.parse(sessionId)

        return try {
            val claims = processor.process(jwt, null)
            UserId.parse(claims.subject)
        } catch (e: Exception) {
            logger.debug(e) { "Failed to process JWT" }
            null
        }
    }
}