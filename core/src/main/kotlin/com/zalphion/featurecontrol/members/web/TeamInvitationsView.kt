package com.zalphion.featurecontrol.members.web

import com.zalphion.featurecontrol.web.invitationsUri
import com.zalphion.featurecontrol.members.Member
import com.zalphion.featurecontrol.members.MemberDetails
import com.zalphion.featurecontrol.teams.Team
import com.zalphion.featurecontrol.web.searchBar
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.onClick
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.html.ul
import kotlin.collections.plus

fun FlowContent.teamInvitations(team: Team, invitations: List<MemberDetails>) = div {
    attributes["x-data"] = "{ filter: ''}"

    val modalId = createMemberModal(team)

    nav("uk-navbar-container uk-navbar-transparent") {
        attributes["uk-navbar"] = ""

        div("uk-navbar-left") {
            searchBar("filter", "Search") {
                classes + "uk-navbar-item"
            }
            div("uk-navbar-item") {
                button(type = ButtonType.button, classes = "uk-button uk-button-primary") {
                    onClick = "UIkit.modal('#$modalId').show()"
                    span {
                        attributes["uk-icon"] = "icon: mail"
                    }
                    +"Invite"
                }
            }
        }
    }

    table("uk-table uk-table-hover") {
        thead {
            tr {
                th { +"Name"}
                th { +"Email" }
                th { +"Role" }
                th { +"Expires" }
            }
        }
        tbody {
            for (details in invitations) {
                val (member, user) = details
                tr {
                    val searchTerms = "${user.emailAddress}${user.userName.orEmpty()}".lowercase().replace("'", "\"")
                    attributes["x-show"] = "'$searchTerms'.toLowerCase().includes(filter.toLowerCase())"
                    td { +user.userName.orEmpty() }
                    td { +user.emailAddress.value }
                    td { +member.role.toString() }
                    td("timestamp") { +member.invitationExpiresOn.toString() }
                    td {
                        ul("uk-iconnav") {
                            li { resendInvitation(member) }
                            li { updatePermissionsButton(details) }
                            li { removeMemberButton(details) }
                        }
                    }
                }
            }
        }
    }
}

private fun FlowContent.resendInvitation(member: Member) {
    form(method = FormMethod.post, action = invitationsUri(member.teamId, member.userId).toString()) {
        button(type = ButtonType.submit, classes = "uk-icon-button") {
            attributes["uk-icon"] = "icon: refresh"
            attributes["uk-tooltip"] = "Resend Invitation"
        }
    }
}