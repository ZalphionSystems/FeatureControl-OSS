package com.zalphion.featurecontrol.configs

import com.zalphion.featurecontrol.features.EnvironmentName
import com.zalphion.featurecontrol.applications.AppId

data class ConfigEnvironment(
    val appId: AppId,
    val environmentName: EnvironmentName,
    val values: Map<PropertyKey, PropertyValue>
)

data class PropertyValue(
    val type: PropertyType,
    val value: String
)