package com.zalphion.featurecontrol.members.web

import com.zalphion.featurecontrol.Core
import com.zalphion.featurecontrol.teams.web.TeamPage
import com.zalphion.featurecontrol.teams.web.teamPage
import com.zalphion.featurecontrol.members.AcceptInvitation
import com.zalphion.featurecontrol.members.CreateMember
import com.zalphion.featurecontrol.members.ListMembersForTeam
import com.zalphion.featurecontrol.members.RemoveMember
import com.zalphion.featurecontrol.members.ResendInvitation
import com.zalphion.featurecontrol.members.UpdateMember
import com.zalphion.featurecontrol.members.UserRole
import com.zalphion.featurecontrol.users.EmailAddress
import com.zalphion.featurecontrol.web.flash.FlashMessageDto
import com.zalphion.featurecontrol.web.PageSpec
import com.zalphion.featurecontrol.web.flash.messages
import com.zalphion.featurecontrol.web.htmlLens
import com.zalphion.featurecontrol.web.invitationsUri
import com.zalphion.featurecontrol.web.principalLens
import com.zalphion.featurecontrol.web.applicationsUri
import com.zalphion.featurecontrol.web.referrerLens
import com.zalphion.featurecontrol.web.samePageError
import com.zalphion.featurecontrol.web.teamIdLens
import com.zalphion.featurecontrol.web.flash.toFlashMessage
import com.zalphion.featurecontrol.web.toIndex
import com.zalphion.featurecontrol.web.userIdLens
import com.zalphion.featurecontrol.web.flash.withMessage
import com.zalphion.featurecontrol.web.flash.withSuccess
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.onFailure
import dev.forkhandles.result4k.recover
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.FormField
import org.http4k.lens.Validator
import org.http4k.lens.enum
import org.http4k.lens.location
import org.http4k.lens.value
import org.http4k.lens.webForm
import kotlin.collections.toList

private object CreateMemberForm {
    val role = FormField.enum<UserRole>().required("role")
    val email = FormField.value(EmailAddress).required("emailAddress")
    val form = Body.webForm(Validator.Strict, role, email).toLens()
}

private object UpdateMemberForm {
    val role = FormField.enum<UserRole>().required("role")
    val form = Body.webForm(Validator.Strict, role).toLens()
}

internal fun Core.showMembers(): HttpHandler = fn@{ request ->
    val principal = principalLens(request)
    val teamId = teamIdLens(request)

    val members = ListMembersForTeam(teamId)
        .invoke(principal, this)
        .onFailure { return@fn request.toIndex().withMessage(it.reason) }
        .toList()
        .filter { it.member.active }

    val model = TeamPage.create(this, principal, teamId, PageSpec.members)
        .onFailure { return@fn request.samePageError(it) }

    Response(Status.OK).with(htmlLens of teamPage(
        model = model,
        messages = request.messages(),
        content = { membersView(it.team.team, members) }
    ))
}

internal fun Core.showInvitations(): HttpHandler = fn@{ request ->
    val principal = principalLens(request)
    val teamId = teamIdLens(request)

    val invitations = ListMembersForTeam(teamId)
        .invoke(principal, this)
        .onFailure { return@fn request.toIndex().withMessage(it.reason) }
        .toList()
        .filter { !it.member.active }

    val model = TeamPage.create(this, principal, teamId, PageSpec.invitations)
        .onFailure { return@fn request.samePageError(it) }

    Response(Status.OK).with(
        htmlLens of teamPage(
            model = model,
            messages = request.messages(),
            content = { teamInvitations(it.team.team, invitations) }
        ))
}

internal fun Core.acceptInvitation(): HttpHandler = { request ->
    val principal = principalLens(request)
    val teamId = teamIdLens(request)
    val userId = userIdLens(request)

    AcceptInvitation(teamId, userId)
        .invoke(principal, this)
        .map {
            Response(Status.SEE_OTHER)
                .location(applicationsUri(teamId)) // FIXME
                .withSuccess("Invitation accepted")
        }
        .recover {
            Response(Status.SEE_OTHER)
                .location(referrerLens(request))
                .withMessage(it)
        }
}

internal fun Core.updateMember(): HttpHandler = { request ->
    val principal = principalLens(request)
    val teamId = teamIdLens(request)
    val userId = userIdLens(request)

    val form = UpdateMemberForm.form(request)

    UpdateMember(teamId, userId, UpdateMemberForm.role(form))
        .invoke(principal, this)
        .map { Response(Status.SEE_OTHER)
            .location(referrerLens(request))
            .withSuccess("Member Updated")
        }.recover {
            Response(Status.SEE_OTHER)
                .location(referrerLens(request))
                .withMessage(it)
        }
}

internal fun Core.deleteMember(): HttpHandler = { request ->
    val principal = principalLens(request)
    val teamId = teamIdLens(request)
    val userId = userIdLens(request)

    RemoveMember(teamId, userId)
        .invoke(principal, this)
        .map {
            Response(Status.SEE_OTHER)
                .location(referrerLens(request))
                .withSuccess("Removed ${it.user.fullName()} from ${it.team.teamName}")
        }.recover {
            Response(Status.SEE_OTHER)
                .location(referrerLens(request))
                .withMessage(it)
        }
}

internal fun Core.resendInvitation(): HttpHandler = { request ->
    val principal = principalLens(request)
    val teamId = teamIdLens(request)
    val userId = userIdLens(request)

    ResendInvitation(teamId, userId).invoke(principal, this).map {
        Response(Status.SEE_OTHER)
            .location(referrerLens(request))
            .withSuccess("Invitation resent")
    }.recover {
        Response(Status.SEE_OTHER)
            .location(referrerLens(request))
            .withMessage(it)
    }
}

internal fun Core.createMember(): HttpHandler = { request ->
    val principal = principalLens(request)
    val teamId = teamIdLens(request)

    val form = CreateMemberForm.form(request)
    val email = CreateMemberForm.email(form)
    val role = CreateMemberForm.role(form)

    val result = CreateMember(teamId, principal.userId, email, role)
        .invoke(principal, this)
        .map { FlashMessageDto(FlashMessageDto.Type.Success, "Invitation sent to $email") }
        .recover { it.toFlashMessage() }

    Response(Status.SEE_OTHER)
        .location(invitationsUri(teamId))
        .withMessage(result)
}