package com.zalphion.featurecontrol.members

import com.zalphion.featurecontrol.core.Members
import com.zalphion.featurecontrol.core.MembersQueries
import com.zalphion.featurecontrol.lib.toPage
import com.zalphion.featurecontrol.teams.TeamId
import com.zalphion.featurecontrol.users.UserId
import dev.andrewohara.utils.pagination.Paginator
import java.time.ZoneOffset

fun MemberStorage.Companion.jdbc(queries: MembersQueries) = object: MemberStorage {
    override fun plusAssign(member: Member) {
        queries.upsert(
            teamId = member.teamId,
            userId = member.userId,
            role = member.role,
            invitedBy = member.invitedBy,
            invitationExpiresOn = member.invitationExpiresOn?.atOffset(ZoneOffset.UTC)
        )
    }

    override fun list(
        userId: UserId,
        pageSize: Int,
    ) = Paginator<Member, TeamId> { cursor ->
        queries.listTeams(userId, cursor ?: TeamId.parse("00000000"), pageSize.plus(1).toLong())
            .executeAsList()
            .map { it.toMember() }
            .toPage(pageSize, Member::teamId)
    }

    override fun list(
        teamId: TeamId,
        pageSize: Int,
    ) = Paginator<Member, UserId> { cursor ->
        queries.listUsers(teamId, cursor ?: UserId.parse("00000000"), pageSize.plus(1).toLong())
            .executeAsList()
            .map { it.toMember() }
            .toPage(pageSize, Member::userId)
    }

    override fun get(teamId: TeamId, userId: UserId) = queries
        .get(teamId, userId)
        .executeAsOneOrNull()
        ?.toMember()

    override fun minusAssign(member: Member) {
        queries.delete(member.teamId, member.userId)
    }
}

private fun Members.toMember() = Member(
    teamId = team_id,
    userId = user_id,
    role = role,
    invitedBy = invited_by,
    invitationExpiresOn = invitation_expires_on?.toInstant()
)