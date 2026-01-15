package com.zalphion.featurecontrol.applications

import com.zalphion.featurecontrol.applicationNotFound
import com.zalphion.featurecontrol.teams.TeamId
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface ApplicationStorage {
    fun list(teamId: TeamId, pageSize: Int): Paginator<Application, AppId>

    operator fun get(appId: AppId): Application?

    operator fun plusAssign(application: Application)
    operator fun minusAssign(application: Application)

    fun getOrFail(appId: AppId) =
        get(appId).asResultOr { applicationNotFound(appId) }

    companion object
}