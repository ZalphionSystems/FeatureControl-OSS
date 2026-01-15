package com.zalphion.featurecontrol.users.web

import com.zalphion.featurecontrol.Core
import com.zalphion.featurecontrol.web.htmlLens
import com.zalphion.featurecontrol.web.NavBar
import com.zalphion.featurecontrol.web.flash.messages
import com.zalphion.featurecontrol.web.principalLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

internal fun Core.showUserSettings(): HttpHandler = fn@{ request ->
    val principal = principalLens(request)

    val navBar = NavBar.get(this, principal)

    Response(Status.OK).with(htmlLens of userPage(
        navBar = navBar,
        messages = request.messages()
    ))
}

