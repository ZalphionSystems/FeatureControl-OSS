package com.zalphion.featurecontrol.members.web

import com.zalphion.featurecontrol.members.MemberDetails
import com.zalphion.featurecontrol.members.UserRole
import com.zalphion.featurecontrol.teams.Team
import com.zalphion.featurecontrol.web.searchBar
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
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

fun FlowContent.membersView(team: Team, members: List<MemberDetails>) = div {
    attributes["x-data"] = "{ filter: ''}"

    nav("uk-navbar-container uk-navbar-transparent") {
        attributes["uk-navbar"] = ""

        div("uk-navbar-left") {
            searchBar("filter", "Search") {
                classes + "uk-navbar-item"
            }
            div("uk-navbar-item") {
                val modalId = createMemberModal(team)
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
                th { +"Name" }
                th { +"Email" }
                th { +"Status" }
                th { +"Role" }
            }
        }
        tbody {
            for (details in members) {
                val (member, user) = details
                tr {
                    val searchTerms = "${user.userName}${user.emailAddress}".lowercase().replace("'", "\"")
                    attributes["x-show"] = "'$searchTerms'.toLowerCase().includes(filter.toLowerCase())"
                    td { +user.userName.orEmpty() }
                    td { +user.emailAddress.value }
                    td { +if (member.active) {
                            "Active"
                        } else {
                            span {
                                attributes["uk-icon"] = "icon: question"
                            }
                            "Pending"
                        }
                    }
                    td { +member.role.toString() }
                    td {
                        ul("uk-iconnav") {
                            if (!member.active) li {
                                a("#", classes = "uk-icon-button") {
                                    attributes["uk-icon"] = "icon: mail"
                                    attributes["uk-tooltip"] = "Resend Invitation"
                                }
                            }
                            li {
                                updatePermissionsButton(details)
                            }
                            if (member.role < UserRole.Admin) {
                                li {
                                    removeMemberButton(details)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}