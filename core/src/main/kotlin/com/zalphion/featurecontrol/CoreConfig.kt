package com.zalphion.featurecontrol

import com.zalphion.featurecontrol.crypto.AppSecret
import com.zalphion.featurecontrol.web.REDIRECT_PATH
import org.http4k.core.Uri
import java.time.Duration

data class CoreConfig(
    val origin: Uri,
    val staticUri: Uri,
    val appSecret: AppSecret,
    val googleClientId: String? = null,
    val csrfTtl: Duration = Duration.ofHours(8),
    var pageSize: Int = 100,
    val sessionLength: Duration = Duration.ofDays(7),
    val secureCookies: Boolean = origin.scheme == "https",
    var invitationRetention: Duration = Duration.ofDays(7),
    val redirectUri: Uri = origin.path(REDIRECT_PATH)
)