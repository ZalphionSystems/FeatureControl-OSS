package com.zalphion.featurecontrol.applications

import com.zalphion.featurecontrol.environmentNotFound
import com.zalphion.featurecontrol.features.EnvironmentName
import com.zalphion.featurecontrol.plugins.Extensions
import com.zalphion.featurecontrol.teams.TeamId
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess

data class Application(
    val teamId: TeamId,
    val appId: AppId,
    val appName: AppName,
    val environments: List<Environment>,
    val extensions: Extensions
)

val Application.environmentNames get() = environments.map { it.name }

fun Application.environmentExistsOrFail(environmentName: EnvironmentName) = if (environmentName in environments.map { it.name }) {
    this.asSuccess()
} else environmentNotFound(appId, environmentName).asFailure()