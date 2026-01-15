package com.zalphion.featurecontrol.members.web

import com.zalphion.featurecontrol.members.UserRole
import kotlinx.html.FlowContent
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.select
import kotlinx.html.span

fun FlowContent.roleSelector(current: UserRole?) {
    label("uk-form-label") {
        span {
            attributes["uk-icon"] = "icon: user"
        }
        +" Role"
    }
    select("uk-select") {
        name = "role"
        required = true

        option {
            selected = current == null
            disabled = true
            value = "" // required to inhibit form submission
            +"(Role)"
        }

        for (role in UserRole.entries) {
            option {
                selected = current == role
                value = role.toString()
                +role.toString()
            }
        }
    }
}