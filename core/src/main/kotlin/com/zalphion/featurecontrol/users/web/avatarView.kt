package com.zalphion.featurecontrol.users.web

import kotlinx.html.FlowContent
import kotlinx.html.SPAN
import kotlinx.html.img
import kotlinx.html.span
import java.net.URI

internal fun FlowContent.avatarView(photoUrl: URI?, size: Int, fn: SPAN.() -> Unit = {}) = span {
    fn()
    if (photoUrl != null) {
        img(classes = "uk-border-circle") {
            width = size.toString()
            height = size.toString()
            src = photoUrl.toString()
        }
    } else {
        span {
            attributes["uk-icon"] = "user"
        }
    }
}