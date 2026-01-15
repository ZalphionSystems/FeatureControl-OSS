package com.zalphion.featurecontrol.web

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.AriaRole
import com.microsoft.playwright.options.Cookie
import com.zalphion.featurecontrol.Core
import com.zalphion.featurecontrol.applications.web.ApplicationsUi
import com.zalphion.featurecontrol.auth.web.createSessionCookie
import com.zalphion.featurecontrol.users.User
import org.http4k.playwright.Http4kBrowser
import java.util.concurrent.TimeUnit

fun Http4kBrowser.asUser(core: Core, user: User, block: (ApplicationsUi) -> Unit = {}): ApplicationsUi {
    val sessionCookie = core.createSessionCookie(user.userId)

    val context = newContext().apply {
        setDefaultTimeout(TimeUnit.SECONDS.toMillis(5).toDouble())
        addCookies(
            listOf(
                Cookie(SESSION_COOKIE_NAME, sessionCookie.value)
                    .setDomain("localhost")
                    .setPath("/")
            )
        )
    }

    return context.newPage()
        .apply { navigate(baseUri.toString()) }
        .let(::ApplicationsUi)
        .also(block)
}

fun Page.getElement(role: AriaRole, name: String): Locator {
    return getByRole(role, Page.GetByRoleOptions().setName(name))
}

fun Locator.getElement(role: AriaRole, name: String): Locator {
    return getByRole(role, Locator.GetByRoleOptions().setName(name))
}

fun Page.getModal(name: String): Locator {
    return getByRole(AriaRole.DIALOG).filter(
        Locator.FilterOptions().setHas(getByRole(AriaRole.HEADING, Page.GetByRoleOptions().setName(name)))
    )
}