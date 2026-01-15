package com.zalphion.featurecontrol.configs

import com.zalphion.featurecontrol.features.EnvironmentName
import com.zalphion.featurecontrol.applications.AppId

interface ConfigStorage {
    operator fun get(appId: AppId): ConfigSpec?
    operator fun get(appId: AppId, environmentName: EnvironmentName): ConfigEnvironment?

    operator fun plusAssign(config: ConfigSpec)
    operator fun plusAssign(environment: ConfigEnvironment)

    operator fun minusAssign(appId: AppId)
    fun delete(appId: AppId, environmentName: EnvironmentName)

    companion object
}

fun ConfigStorage.getOrEmpty(appId: AppId) =
    get(appId) ?: ConfigSpec(appId, emptyMap())

fun ConfigStorage.getOrEmpty(appId: AppId, environmentName: EnvironmentName) =
    get(appId, environmentName) ?: ConfigEnvironment(appId, environmentName, emptyMap())