package com.zalphion.featurecontrol.web

import com.zalphion.featurecontrol.features.EnvironmentName
import com.zalphion.featurecontrol.features.FeatureKey
import com.zalphion.featurecontrol.applications.AppId
import com.zalphion.featurecontrol.teams.TeamId
import com.zalphion.featurecontrol.users.UserId
import org.http4k.core.Uri
import org.http4k.core.appendToPath

data class PageSpec(val name: String, val icon: String) {
    companion object {
        val applications = PageSpec("Applications", "icon: album")
        val features = PageSpec("Features", "icon: cog")
        val config = PageSpec("Config", "icon: file-text")
        val members = PageSpec("Members", "icon: users")
        val invitations = PageSpec("Invitations", "icon: users")
    }
}
data class PageLink(
    val spec: PageSpec,
    val uri: Uri,
    val enabled: Boolean = true,
    val tooltip: String? = null,
)

fun teamUri(teamId: TeamId) = Uri.of("/teams/$teamId")
fun membersUri(teamId: TeamId) = teamUri(teamId).appendToPath("members")
fun membersUri(teamId: TeamId, userId: UserId) = membersUri(teamId).appendToPath(userId.value)
fun invitationsUri(teamId: TeamId) = teamUri(teamId).appendToPath("invitations")
fun invitationsUri(teamId: TeamId, userId: UserId) = invitationsUri(teamId).appendToPath(userId.value)
fun applicationsUri(teamId: TeamId) = teamUri(teamId).appendToPath("applications")
fun applicationUri(appId: AppId) = Uri.of("/applications/$appId")

fun featuresUri(appId: AppId) = applicationUri(appId).appendToPath("features")
fun featureUri(appId: AppId, featureKey: FeatureKey) = featuresUri(appId).appendToPath(featureKey.value)
fun featureUri(appId: AppId, featureKey: FeatureKey, environmentName: EnvironmentName) =
    featureUri(appId, featureKey).appendToPath("environments/$environmentName")

fun configUri(appId: AppId) = applicationUri(appId).appendToPath("config")
fun configUri(appId: AppId, environmentName: EnvironmentName) = configUri(appId).appendToPath(environmentName.value)

