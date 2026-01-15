package com.zalphion.featurecontrol.auth

import com.zalphion.featurecontrol.applications.AppId
import com.zalphion.featurecontrol.features.EnvironmentName

data class EnginePrincipal(
    val appId: AppId,
    val environmentName: EnvironmentName
)