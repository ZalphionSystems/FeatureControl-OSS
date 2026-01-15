package com.zalphion.featurecontrol.members

import com.zalphion.featurecontrol.memberNotFound
import com.zalphion.featurecontrol.teams.TeamId
import com.zalphion.featurecontrol.users.UserId
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface MemberStorage {
    operator fun plusAssign(member: Member)
    fun list(userId: UserId, pageSize: Int): Paginator<Member, TeamId>
    fun list(teamId: TeamId, pageSize: Int): Paginator<Member, UserId>
    operator fun get(teamId: TeamId, userId: UserId): Member?
    operator fun minusAssign(member: Member)

    fun getOrFail(teamId: TeamId, userId: UserId) = get(teamId, userId).asResultOr { memberNotFound(teamId, userId) }

    companion object
}