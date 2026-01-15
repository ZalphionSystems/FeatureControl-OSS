package com.zalphion.featurecontrol.auth.web

import com.zalphion.featurecontrol.CoreConfig
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.script
import org.http4k.core.Uri

fun FlowContent.socialLogin(config: CoreConfig) {
    if (config.googleClientId != null) {
        googleSignIn(config.googleClientId, config.redirectUri)
    }
}

private fun FlowContent.googleSignIn(clientId: String, redirectUri: Uri) {
    script {
        src = "https://accounts.google.com/gsi/client"
        async = true
    }
    div {
        id = "g_id_onload"
        attributes["data-client_id"] = clientId
        attributes["data-login_uri"] = redirectUri.toString()
        attributes["data-ux_mode"] = "redirect"
        attributes["data-auto_prompt"] = "false"
    }
    div("g_id_signin") {
        attributes["data-type"] = "standard"
        attributes["data-size"] = "large"
        attributes["data-theme"] = "outline"
        attributes["data-text"] = "sign_in_with"
        attributes["data-shape"] = "rectangular"
        attributes["data-logo_alignment"] = "left"
    }
}