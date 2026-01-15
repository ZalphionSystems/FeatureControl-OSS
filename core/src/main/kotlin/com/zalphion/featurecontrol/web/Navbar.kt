package com.zalphion.featurecontrol.web

import com.zalphion.featurecontrol.teams.web.teamSelector
import com.zalphion.featurecontrol.users.web.avatarView
import com.zalphion.featurecontrol.APP_NAME
import com.zalphion.featurecontrol.Core
import com.zalphion.featurecontrol.AppError
import com.zalphion.featurecontrol.members.ListMembersForUser
import com.zalphion.featurecontrol.members.MemberDetails
import com.zalphion.featurecontrol.teamNotFound
import com.zalphion.featurecontrol.teams.TeamId
import com.zalphion.featurecontrol.users.User
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.onFailure
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.onClick
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.ul
import kotlin.collections.plus
import kotlin.collections.set
import kotlin.collections.toList

data class NavBar<T>(
    val principal: User,
    val memberships: List<MemberDetails>,
    val selectedTeam: T,
    val pages: Collection<PageLink>,
    val selectedPage: PageSpec?
) {
    companion object {
        fun get(
            core: Core,
            principal: User,
            teamId: TeamId,
            selected: PageSpec?
        ): Result4k<NavBar<MemberDetails>, AppError> {
            val authorizedTeams = ListMembersForUser(principal.userId)
                .invoke(principal, core)
                .onFailure { error(it) }
                .toList()

            val team = authorizedTeams
                .find { it.team.teamId == teamId }
                ?: return teamNotFound(teamId).asFailure()

            return NavBar(
                principal = principal,
                memberships = authorizedTeams,
                selectedTeam = team,
                pages = core.getPages(teamId),
                selectedPage = selected
            ).asSuccess()
        }

        fun get(
            core: Core,
            principal: User
        ): NavBar<MemberDetails?> {
            val authorizedTeams = ListMembersForUser(principal.userId)
                .invoke(principal, core)
                .onFailure { error(it) }
                .toList()

            return NavBar(
                principal = principal,
                memberships = authorizedTeams,
                selectedTeam = null,
                pages = emptyList(),
                selectedPage = null
            )
        }
    }
}

fun FlowContent.navbar(model: NavBar<out MemberDetails?>) = with(model) {
    nav("uk-navbar-container uk-navbar-transparent uk-background-primary uk-light") {
        attributes["uk-navbar"] = ""
        div("uk-navbar-left") {
            ul("uk-navbar-nav") {
                div("uk-navbar-item uk-logo") {
                    +APP_NAME
                }
                for (page in pages) {
                    pageLink(page, selectedPage)
                }
            }
        }

        div("uk-navbar-right") {
            div("uk-navbar-item") {
                teamSelector(memberships.map { it.team },selectedTeam)
            }
            div("uk-navbar-item") {
                userWidget(principal)
            }
        }
    }
}

private fun UL.pageLink(page: PageLink, selectedPage: PageSpec?) {
    li {
        if (page.spec == selectedPage) {
            classes + "uk-active"
        }
        a(page.uri.toString(), classes = "uk-navbar-item") {
            span("uk-icon uk-margin-xsmall-right") {
                attributes["uk-icon"] = page.spec.icon
            }
            +page.spec.name
        }
    }
}

private fun FlowContent.userWidget(principal: User) {
    button(type = ButtonType.button, classes = "uk-button uk-button-default uk-border-pill uk-padding-remove-left") {
        style = "padding-right: 10px;"
        avatarView(principal.photoUrl, 40) {
            classes + "uk-margin-small-right"
        }
        +(principal.userName ?: principal.emailAddress.value)
        span {
            attributes["uk-drop-parent-icon"] = "ratio: 1.5"
        }
    }

    div("uk-navbar-dropdown") {
        attributes["uk-dropdown"] = "mode: click;"

        form(LOGOUT_PATH, method = FormMethod.post) {
            id = "logout"
            style = "display: none;"
        }

        ul("uk-nav uk-navbar-dropdown-nav") {
            li {
                a(USER_SETTINGS_PATH) {
                    span {
                        attributes["uk-icon"] = "icon: user"
                    }
                    +"User Settings"
                }
            }
            li("uk-nav-divider")
            li {
                a("#") {
                    onClick = "event.preventDefault(); document.getElementById('logout').submit();"
                    span {
                        attributes["uk-icon"] = "icon: sign-out"
                    }
                    +"Logout"
                }
            }
        }
    }
}