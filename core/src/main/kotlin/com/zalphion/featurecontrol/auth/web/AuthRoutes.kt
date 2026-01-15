package com.zalphion.featurecontrol.auth.web

import com.zalphion.featurecontrol.users.UserService
import com.zalphion.featurecontrol.web.INDEX_PATH
import com.zalphion.featurecontrol.web.LOGIN_PATH
import com.zalphion.featurecontrol.web.LOGOUT_PATH
import com.zalphion.featurecontrol.web.REDIRECT_PATH
import com.zalphion.featurecontrol.web.SESSION_COOKIE_NAME
import com.zalphion.featurecontrol.web.htmlLens
import com.zalphion.featurecontrol.web.toIndex
import com.zalphion.featurecontrol.Core
import com.zalphion.featurecontrol.users.UserId
import dev.forkhandles.result4k.onFailure
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.form
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.SameSite
import org.http4k.core.cookie.invalidateCookie
import org.http4k.core.cookie.replaceCookie
import org.http4k.core.with
import org.http4k.routing.bind
import org.http4k.routing.routes

fun Core.authRoutes(core: Core, socialAuth: SocialAuthorizer) = routes(
    LOGIN_PATH bind Method.GET to {
        Response(Status.OK).with(htmlLens of loginView())
    },
    REDIRECT_PATH bind Method.POST to fn@{ request ->
        val userData = request.form("credential")
            ?.let(socialAuth::invoke)
            ?: return@fn Response(Status.UNAUTHORIZED)

        val user = UserService(core)
            .getOrCreate(userData)
            .onFailure { error(it.reason) }

        request.toIndex().replaceCookie(createSessionCookie(user.userId))
    },
    LOGOUT_PATH bind Method.POST to {
        it.toIndex().invalidateCookie(SESSION_COOKIE_NAME, path = INDEX_PATH)
    }
)

fun Core.createSessionCookie(userId: UserId): Cookie {
    val (sessionId, expires) = sessions.create(userId)
    return Cookie(
        name = SESSION_COOKIE_NAME,
        value = sessionId,
        secure = config.secureCookies,
        sameSite = SameSite.Lax,
        httpOnly = true,
        expires = expires,
        path = INDEX_PATH
    )
}