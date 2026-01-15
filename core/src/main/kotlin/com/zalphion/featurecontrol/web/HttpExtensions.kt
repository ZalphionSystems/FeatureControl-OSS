package com.zalphion.featurecontrol.web

import com.zalphion.featurecontrol.web.flash.FlashMessageDto
import com.zalphion.featurecontrol.web.flash.withMessage
import com.zalphion.featurecontrol.AppError
import dev.forkhandles.result4k.Failure
import kotlinx.html.FORM
import kotlinx.html.InputType
import kotlinx.html.input
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.filter.flash
import org.http4k.filter.withFlash
import org.http4k.lens.location
import org.http4k.routing.asRouter

private const val FORM_METHOD_NAME = "_method"

fun Request.toIndex() = Response(Status.FOUND)
    .location(Uri.of(INDEX_PATH))
    .let { flash()?.let(it::withFlash) ?: it }

private fun Request.richFormMethod() = form(FORM_METHOD_NAME)?.let { Method.valueOf(it.uppercase()) }
    ?: query(FORM_METHOD_NAME)?.let { Method.valueOf(it.uppercase()) }
    ?: method

fun FORM.withRichMethod(method: Method) {
    input(InputType.hidden) {
        name = FORM_METHOD_NAME
        value = method.toString()
    }
}

fun Request.samePage(message: FlashMessageDto) = Response(Status.SEE_OTHER)
    .location(referrerLens(this))
    .withMessage(message)

fun Request.samePageError(error: AppError) = Response(Status.SEE_OTHER)
    .location(referrerLens(this))
    .withMessage(error)

fun Request.samePageError(failure: Failure<AppError>) = samePageError(failure.reason)

val isRichPut = { request: Request ->
    request.method == Method.POST && request.richFormMethod() == Method.PUT
}.asRouter()

val isRichDelete = { request: Request ->
    request.method == Method.POST && request.richFormMethod() == Method.DELETE
}.asRouter()