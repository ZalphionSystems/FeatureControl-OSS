package com.zalphion.featurecontrol.users.web

import com.zalphion.featurecontrol.members.Member
import com.zalphion.featurecontrol.members.MemberDetails
import com.zalphion.featurecontrol.members.UserRole
import com.zalphion.featurecontrol.teams.Team
import com.zalphion.featurecontrol.web.flash.FlashMessageDto
import com.zalphion.featurecontrol.web.NavBar
import com.zalphion.featurecontrol.web.deleteModal
import com.zalphion.featurecontrol.web.invitationsUri
import com.zalphion.featurecontrol.web.membersUri
import com.zalphion.featurecontrol.web.navbar
import com.zalphion.featurecontrol.web.pageSkeleton
import com.zalphion.featurecontrol.web.withRichMethod
import com.zalphion.featurecontrol.Core
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.aside
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h3
import kotlinx.html.li
import kotlinx.html.main
import kotlinx.html.nav
import kotlinx.html.onClick
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.html.ul
import org.http4k.core.Method

fun Core.userPage(
    navBar: NavBar<MemberDetails?>,
    messages: List<FlashMessageDto>,
    subTitle: String? = null
) = pageSkeleton(messages, subTitle) {
    navbar(navBar)

    div("uk-flex uk-height-viewport") {
        aside("uk-width-medium uk-background-muted uk-padding-small uk-overflow-auto") {
            style = "box-shadow: 2px 0 5px rgba(0, 0, 0, 0.05);"

            h3("uk-logo") {
                span("uk-margin-small-right") {
                    attributes["uk-icon"] = "icon: users"
                }
                +"User Settings"
            }
        }

        main("uk-width-expand uk-padding-small uk-overflow-auto") {
            teams(navBar.memberships.filter { details -> details.member.active })
            invitations(navBar.memberships.filter { details -> !details.member.active })
        }
    }
}

private fun FlowContent.teams(teams: List<MemberDetails>) {
    nav("uk-navbar-container uk-navbar-transparent") {
        attributes["uk-navbar"] = ""

        div("uk-navbar-left") {
            h3("uk-navbar-item uk-logo uk-margin-remove-bottom") {
                span {
                    attributes["uk-icon"] = "icon: users"
                }
                +"Teams"
            }
        }
    }

    table("uk-table uk-table-hover") {
        thead {
            tr {
                th { +"Team" }
                th { +"Role" }
            }
        }
        tbody {
            for ((member, _, _, team) in teams) {
                tr {
                    td { +team.teamName.value }
                    td { +member.role.toString() }
                    td {
                        ul("uk-iconnav") {
                            li { leaveTeam(team, member) }
                        }
                    }
                }
            }
        }
    }
}

private fun FlowContent.invitations(invitations: List<MemberDetails>) {
    nav("uk-navbar-container uk-navbar-transparent") {
        attributes["uk-navbar"] = ""

        div("uk-navbar-left") {
            h3("uk-navbar-item uk-logo uk-margin-remove-bottom") {
                span {
                    attributes["uk-icon"] = "icon: mail"
                }
                +"Invitations"
            }
        }
    }

    table("uk-table uk-table-hover") {
        thead {
            tr {
                th { +"Team" }
                th { +"Role" }
                th { +"Expires" }
            }
        }
        tbody {
            for (details in invitations) {
                tr {
                    td { +details.team.teamName.value }
                    td { +details.member.role.toString() }
                    td("timestamp") { +details.member.invitationExpiresOn.toString() }
                    td {
                        ul("uk-iconnav") {
                            acceptInvitation(details.team)
                            revokeInvitation(details)
                        }
                    }
                }
            }
        }
    }
}

private fun FlowContent.acceptInvitation(team: Team) {
    form(invitationsUri(team.teamId).toString(), method = FormMethod.post) {
        withRichMethod(Method.POST)
        button(type = ButtonType.submit, classes = "uk-icon-button") {
            attributes["uk-icon"] = "icon: check"
            attributes["uk-tooltip"] = "Accept Invitation"
        }
    }
}

private fun FlowContent.leaveTeam(team: Team, member: Member) {
    val canLeave = member.role != UserRole.Admin
    form(membersUri(team.teamId).toString(), method = FormMethod.post) {
        withRichMethod(Method.DELETE)
        button(type = ButtonType.submit, classes = "uk-icon-button") {
            attributes["uk-icon"] = "icon: sign-out"
            attributes["uk-tooltip"] = if (canLeave) "Leave Team" else "Cannot leave while an Admin"
            if (!canLeave) attributes["disabled"] = ""
        }
    }
}

fun FlowContent.revokeInvitation(details: MemberDetails) {
    val modalId = deleteModal(
        resourceName = "Invitation from ${details.invitedBy?.userName} (${details.invitedBy?.emailAddress}) to ${details.team.teamName}",
        action = membersUri(details.team.teamId, details.user.userId),
    )
    button(type = ButtonType.button, classes = "uk-icon-button") {
        attributes["uk-icon"] = "icon: trash"
        attributes["uk-tooltip"] = "Revoke Invitation"
        onClick = "UIkit.modal('#$modalId').show()"
    }
}