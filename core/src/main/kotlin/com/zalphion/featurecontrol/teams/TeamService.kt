package com.zalphion.featurecontrol.teams

import com.zalphion.featurecontrol.Core
import com.zalphion.featurecontrol.AppError
import com.zalphion.featurecontrol.members.Member
import com.zalphion.featurecontrol.members.UserRole
import com.zalphion.featurecontrol.ActionAuth
import com.zalphion.featurecontrol.ServiceAction
import com.zalphion.featurecontrol.users.UserId
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.peek

class CreateTeam(
    val ownerId: UserId,
    val data: TeamCreateUpdateData
): ServiceAction<Team>({ _, _ -> Unit.asSuccess() }) {
    override fun execute(core: Core): Result4k<Team, AppError> {
        val team = Team(
            teamId = TeamId.random(core.random),
            teamName = data.teamName,
        )
        val member = Member(
            teamId = team.teamId,
            userId = ownerId,
            role = UserRole.Admin,
            invitedBy = null,
            invitationExpiresOn = null
        )

        core.teams += team
        core.members += member

        return team.asSuccess()
    }
}

class UpdateTeam(
    val teamId: TeamId,
    val data: TeamCreateUpdateData
): ServiceAction<Team>(ActionAuth.byTeam(teamId, UserRole.Admin)) {
    override fun execute(core: Core) = core
        .teams.getOrFail(teamId)
        .map { it.copy(teamName = data.teamName) }
        .peek(core.teams::plusAssign)
}