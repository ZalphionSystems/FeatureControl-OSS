package com.zalphion.featurecontrol.teams

import com.zalphion.featurecontrol.core.Teams
import com.zalphion.featurecontrol.core.TeamsQueries

fun TeamStorage.Companion.jdbc(queries: TeamsQueries) = object: TeamStorage {

    override fun get(teamId: TeamId) = queries
        .get(teamId)
        .executeAsOneOrNull()
        ?.toTeam()

    override fun batchGet(ids: Collection<TeamId>): List<Team>{
        if (ids.isEmpty()) return emptyList()
        return queries
            .getAll(ids)
            .executeAsList()
            .map { it.toTeam() }
    }

    override fun plusAssign(team: Team) {
        queries.upsert(
           id = team.teamId,
           name = team.teamName,
        )
    }

    override fun minusAssign(team: Team) {
        queries.delete(team.teamId)
    }
}

private fun Teams.toTeam() = Team(
    teamId = team_id,
    teamName = team_name,
)