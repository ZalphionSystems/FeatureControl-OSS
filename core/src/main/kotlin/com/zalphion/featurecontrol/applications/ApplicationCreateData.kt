package com.zalphion.featurecontrol.applications

import com.zalphion.featurecontrol.plugins.Extensions
import com.zalphion.featurecontrol.teams.TeamId
import kotlin.random.Random

data class ApplicationCreateData(
    val appName: AppName,
    val environments: List<Environment>,
    val extensions: Extensions
) {
    fun toModel(teamId: TeamId, random: Random) = Application(
        teamId = teamId,
        appId =  AppId.random(random),
        appName = appName,
        environments = environments,
        extensions = extensions
    )
}