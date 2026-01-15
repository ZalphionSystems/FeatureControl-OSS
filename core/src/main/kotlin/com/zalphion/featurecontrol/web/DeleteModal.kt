package com.zalphion.featurecontrol.web

import kotlinx.html.ButtonType
import kotlinx.html.FORM
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.id
import kotlinx.html.p
import kotlinx.html.span
import org.http4k.core.Method
import org.http4k.core.Uri
import java.util.UUID

fun FlowContent.deleteModal(
    resourceName: String,
    action: Uri,
    formContent: FORM.() -> Unit = {}
): String {
    val modalId = "delete-${UUID.randomUUID()}"

    div("uk-modal uk-modal-container") {
        id = modalId

        div("uk-modal-dialog") {

            div("uk-modal-header") {
                button(type = ButtonType.button, classes = "uk-modal-close-default") {
                    attributes["uk-close"] = ""
                }

                h2("uk-modal-title") {
                    span("uk-text-danger uk-margin-small-right") {
                        attributes["uk-icon"] = "icon: warning; ratio: 2;"
                    }
                    +"Delete $resourceName?"
                }
            }


            div("uk-modal-body") {
                p {
                    +"Are you sure you want to delete $resourceName?"
                }
            }

            div("uk-modal-footer") {
                form(action.toString(), method = FormMethod.post) {
                    withRichMethod(Method.DELETE)
                    formContent()

                    button(type = ButtonType.submit, classes = "uk-button uk-button-danger") {
                        +"Delete"
                    }
                    button(type = ButtonType.button, classes = "uk-button uk-button-default uk-modal-close") {
                        +"Cancel"
                    }
                }
            }
        }
    }

    return modalId
}