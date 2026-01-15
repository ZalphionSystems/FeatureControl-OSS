package com.zalphion.featurecontrol.web

import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.input
import kotlinx.html.p
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.tfoot
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import org.http4k.lens.BiDiMapping

class TableElementSchema(
    val name: String,
    val key: String? = null,
    val type: InputType = InputType.text,
    val required: Boolean = true,
    val placeholder: String? = null,
    val headerClasses: String? = null
)

fun <Element: Any> FlowContent.tableForm(
    inputName: String,
    rowAriaLabel: String,
    schema: List<TableElementSchema>,
    elements: List<Element>,
    mapper: BiDiMapping<String, List<Element>>,
    debug: Boolean = false
) = div {
    attributes["x-data"] = """{
        $inputName: ${mapper(elements)}
    }"""

    input(InputType.hidden, name = inputName) {
        attributes[":value"] = "JSON.stringify($inputName)"
    }

    if (debug) {
        p {
            attributes["x-text"] = $$"JSON.stringify($data, null, 2)"
        }
    }

    table("uk-table uk-table-middle") {
        thead {
            tr {
                for (schemaElement in schema) {
                    th(classes = schemaElement.headerClasses) {
                        +schemaElement.name
                    }
                }
            }
        }

        tbody {
            template {
                attributes["x-for"] = "(element, index) in $inputName"
                attributes[":key"] = "index"

                tr {
                    for (schemaElement in schema) {
                        td {
                            val clazz = when(schemaElement.type) {
                                InputType.checkBox -> "uk-checkbox"
                                InputType.radio -> "uk-radio"
                                else -> "uk-input"
                            }
                            input(schemaElement.type, classes = clazz) {
                                attributes["x-model"] = if (schemaElement.key == null) "element" else "element.${schemaElement.key}"
                                attributes["aria-label"] = schemaElement.name
                                placeholder = schemaElement.placeholder ?: ""
                                required = schemaElement.required
                            }
                        }
                    }

                    td { // remove button
                        button(type = ButtonType.button, classes = "uk-icon-button uk-button-default") {
                            attributes["aria-label"] = "Remove row"
                            attributes["uk-icon"] = "trash"
                            onClick("$inputName.splice(index, 1)")
                        }
                    }
                }
            }
        }

        tfoot {
            tr {
                td { // New Row
                    button(type = ButtonType.button, classes = "uk-icon-button uk-button-default") {
                        attributes["uk-icon"] = "plus"
                        attributes["uk-tooltip"] = "Add $rowAriaLabel"
                        attributes["aria-label"] = "Add $rowAriaLabel"
                        onClick("$inputName.push({})")
                    }
                }
            }
        }
    }
}