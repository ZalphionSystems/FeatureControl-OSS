package com.zalphion.featurecontrol.applications

import com.zalphion.featurecontrol.core.Applications
import com.zalphion.featurecontrol.core.ApplicationsQueries
import com.zalphion.featurecontrol.features.EnvironmentName
import com.zalphion.featurecontrol.lib.Colour
import com.zalphion.featurecontrol.lib.toPage
import com.zalphion.featurecontrol.plugins.Extensions
import com.zalphion.featurecontrol.teams.TeamId
import dev.andrewohara.utils.pagination.Paginator
import se.ansman.kotshi.JsonSerializable

fun ApplicationStorage.Companion.jdbc(queries: ApplicationsQueries) = object: ApplicationStorage {

    override fun list(teamId: TeamId, pageSize: Int) = Paginator<Application, AppId> { cursor ->
        queries.list(teamId, cursor ?: AppId.parse("00000000"), pageSize.plus(1).toLong())
            .executeAsList()
            .map { it.toModel() }
            .toPage(pageSize, Application::appId)
    }

    override fun get(appId: AppId) = queries
        .get(appId)
        .executeAsOneOrNull()
        ?.toModel()

    override fun plusAssign(application: Application) {
        queries.upsert(
            team_id = application.teamId,
            app_id = application.appId,
            app_name = application.appName,
            environments = application.environments.map { it.toJdbc() }.toTypedArray(),
            extensions = application.extensions
        )
    }

    override fun minusAssign(application: Application) {
        queries.delete(application.teamId, application.appId)
    }
}

private fun Applications.toModel() = Application(
    teamId = team_id,
    appId = app_id,
    appName = app_name,
    environments = environments.map { it.toModel() },
    extensions = extensions
)

@JsonSerializable
data class JdbcEnvironment(
    val name: EnvironmentName,
    val description: String,
    val colour: Colour,
    val extensions: Extensions = emptyMap() // default for back-compat
)

private fun JdbcEnvironment.toModel() = Environment(
    name = name,
    description = description,
    colour = colour,
    extensions = extensions
)

private fun Environment.toJdbc() = JdbcEnvironment(
    name = name,
    description = description,
    colour = colour,
    extensions = extensions
)
