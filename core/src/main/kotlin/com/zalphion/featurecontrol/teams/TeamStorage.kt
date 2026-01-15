package com.zalphion.featurecontrol.teams

import com.zalphion.featurecontrol.teamNotFound
import dev.forkhandles.result4k.asResultOr

interface TeamStorage {
    operator fun get(teamId: TeamId): Team?
    fun batchGet(ids: Collection<TeamId>): Collection<Team>

    operator fun plusAssign(team: Team)
    operator fun minusAssign(team: Team)
    fun getOrFail(teamId: TeamId) = get(teamId).asResultOr { teamNotFound(teamId) }

    companion object
}

