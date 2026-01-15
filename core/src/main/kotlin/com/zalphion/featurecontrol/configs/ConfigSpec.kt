package com.zalphion.featurecontrol.configs

import com.zalphion.featurecontrol.applications.AppId
import dev.forkhandles.result4k.asResultOr

data class ConfigSpec(
    val appId: AppId,
    val properties: Map<PropertyKey, Property>
) {
    fun getOrFail(key: PropertyKey) = properties[key].asResultOr { propertyNotFound(appId, key) }
}