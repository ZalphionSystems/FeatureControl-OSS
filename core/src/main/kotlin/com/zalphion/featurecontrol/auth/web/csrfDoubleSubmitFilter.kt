package com.zalphion.featurecontrol.auth.web

import com.zalphion.featurecontrol.AppError
import com.zalphion.featurecontrol.web.toIndex
import com.zalphion.featurecontrol.web.flash.withMessage
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.parseOrNull
import org.http4k.core.Filter
import org.http4k.core.Method
import org.http4k.core.body.form
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.replaceCookie
import java.time.Duration
import java.util.HexFormat
import kotlin.random.Random

const val CSRF_FORM_PARAM = "_csrf"
const val CSRF_COOKIE_NAME = "CSRF-Token"

private val csrfError = AppError("csrf.token.invalid")

/**
 * CSRF Double Submit relies on the submitted form containing a token matching a paired cookie.
 * The cookie has a configurable TTL via maxAge.
 * The token is completely opaque and stateless.
 */
fun csrfDoubleSubmitFilter(
    secureRandom: Random,
    secureCookies: Boolean,
    ttl: Duration
): Filter = Filter { next ->
    req@{ request ->
        val cookieValue = request.cookie(CSRF_COOKIE_NAME)
            ?.let { CsrfToken.parseOrNull(it.value) }

        if (request.method == Method.POST) {
            val formValue = request.form(CSRF_FORM_PARAM)
                ?.let { CsrfToken.parseOrNull(it) }

            if (cookieValue == null || cookieValue != formValue) {
                return@req request.toIndex().withMessage(csrfError)
            }
        }

        if (cookieValue == null) {
            val newToken = CsrfToken.random(secureRandom).toString()
            val cookie = Cookie(
                name = CSRF_COOKIE_NAME,
                sameSite = SameSite.Strict,
                httpOnly = false, // JS need to read cookie to set form inputs
                secure = secureCookies,
                value = newToken,
                maxAge = ttl.toSeconds(),
                path = "/"
            )
            next(request)
                .replaceCookie(cookie)
        } else {
            next(request)
        }
    }
}

private const val CSRF_TOKEN_LENGTH = 32
private val hex = HexFormat.of()

private class CsrfToken private constructor(value: String): StringValue(value) {

    companion object: StringValueFactory<CsrfToken>( ::CsrfToken) {
        fun random(random: Random) = random.nextBytes(CSRF_TOKEN_LENGTH)
            .let(hex::formatHex)
            .let(::of)
    }
}