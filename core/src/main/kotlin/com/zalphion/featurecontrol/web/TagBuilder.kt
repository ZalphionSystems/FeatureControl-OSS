package com.zalphion.featurecontrol.web

import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.span
import kotlinx.html.style

fun FlowContent.tagBuilder(
    result: String,
    temp: String,
    prompt: String? = null
) {
    val submitElement = """
        if ($temp.trim() !== '' && !$result.includes($temp.trim())) {
            $result.push($temp.trim());
            $temp = '';
        }
    """.trimIndent()
    div {
        // tag builder
        div {
            input(InputType.text, classes = "uk-input uk-form-width-medium") {
                id = temp
                if (prompt != null) placeholder = prompt
                attributes["x-model"] = temp
                attributes[":class"] = "{'uk-form-danger': $result.includes($temp.trim())}"
                onEnter(submitElement)
                onTab(submitElement)
                onSpace(submitElement)
            }
            a("#", classes = "uk-icon-button") {
                attributes["uk-icon"] = "plus"
                onClick(submitElement)
            }
        }

        // Tag List
        template {
            attributes["x-for"] = "(tag, index) in $result"
            attributes[":key"] = "tag + index"

            span {
                span("uk-label") {
                    style = "margin-right: 0.5em;"
                    span {
                        attributes["x-text"] = "tag"
                    }
                    a("#") {
                        attributes["uk-icon"] = "close"
                        onClick("$result.splice(index, 1)")
                    }
                }
            }
        }

    }
}