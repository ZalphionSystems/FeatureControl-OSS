package com.zalphion.featurecontrol.members.web

import com.zalphion.featurecontrol.members.MemberDetails
import com.zalphion.featurecontrol.web.confirmCancelButtons
import com.zalphion.featurecontrol.web.membersUri
import com.zalphion.featurecontrol.web.withRichMethod
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.id
import kotlinx.html.onClick
import kotlinx.html.p
import org.http4k.core.Method

fun FlowContent.updatePermissionsButton(member: MemberDetails) {
    val modalId = updatePermissionsModal(member)
    a("#", classes = "uk-icon-button") {
        attributes["uk-icon"] = "icon: settings"
        attributes["uk-tooltip"] = "Update Permissions"
        onClick = "UIkit.modal('#$modalId').show()"
    }
}

private fun FlowContent.updatePermissionsModal(member: MemberDetails): String {
    val modalId = "updateRole_${member.team.teamId}_${member.user.userId}"
    div("uk-modal uk-modal-container") {
        id = modalId

        div("uk-modal-dialog") {
            form(method = FormMethod.post, action = membersUri(member.team.teamId, member.user.userId).toString(), classes = "uk-form-stacked") {
                withRichMethod(Method.PUT)

                div("uk-modal-body") {
                    h2("uk-modal-title") { +"Update Role" }

                    button(type = ButtonType.button, classes = "uk-modal-close-default") {
                        attributes["uk-close"] = ""
                    }

                    p {
                        +"Update ${member.user.fullName()} in ${member.team.teamName}"
                    }

                    roleSelector(member.member.role)
                }

                div("uk-modal-footer") {
                    confirmCancelButtons("Update")
                }
            }
        }
    }

    return modalId
}