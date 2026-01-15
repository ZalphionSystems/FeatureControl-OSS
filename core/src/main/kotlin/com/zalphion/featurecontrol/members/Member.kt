package com.zalphion.featurecontrol.members

import com.zalphion.featurecontrol.teams.TeamId
import com.zalphion.featurecontrol.users.UserId
import java.time.Instant

data class Member(
    val teamId: TeamId,
    val userId: UserId,
    val invitedBy: UserId?,
    val role: UserRole,
    val invitationExpiresOn: Instant?
) {
    val active get() = invitationExpiresOn == null
}