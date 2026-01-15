package com.zalphion.featurecontrol.members

import com.zalphion.featurecontrol.teams.Team
import com.zalphion.featurecontrol.users.User

data class MemberDetails(
    val member: Member,
    val user: User,
    val invitedBy: User?,
    val team: Team
)