package com.zalphion.featurecontrol.auth.web

import com.zalphion.featurecontrol.users.UserCreateData

fun interface SocialAuthorizer {
    operator fun invoke(idToken: String): UserCreateData?

    infix fun or(other: SocialAuthorizer) =
        SocialAuthorizer { invoke(it) ?: other(it) }

    companion object {
        fun noOp() = SocialAuthorizer { null }
    }
}