package com.zalphion.featurecontrol.configs

import com.zalphion.featurecontrol.AppError
import com.zalphion.featurecontrol.applications.AppId

fun propertyNotFound(appId: AppId, key: PropertyKey) = AppError(
    messageCode = "propertyNotFound",
    messageArguments = mapOf(
        "applicationId" to appId.value,
        "propertyKey" to key.value
    )
)