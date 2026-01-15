package com.zalphion.featurecontrol.web.flash

import com.zalphion.featurecontrol.AppError
import io.github.oshai.kotlinlogging.KotlinLogging
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.filter.flash
import org.http4k.filter.withFlash

private val logger = KotlinLogging.logger { }

fun Request.messages() = flash().parseMessages().toList()

private fun String?.parseMessages(): Array<FlashMessageDto> {
    if (isNullOrBlank()) return arrayOf()
    return try {
        FlashMessageDto.adapter(this)
    } catch (e: Exception) {
        logger.error(e) { "Error parsing flash messages: $this" }
        arrayOf()
    }
}

fun Response.withMessage(message: FlashMessageDto): Response {
    val existing = flash().parseMessages()
    return withFlash(FlashMessageDto.adapter(existing + message))
}

fun Response.withMessage(message: String, type: FlashMessageDto.Type = FlashMessageDto.Type.Info) =
    withMessage(FlashMessageDto(type, message))

fun Response.withSuccess(message: String) =
    withMessage(message, FlashMessageDto.Type.Success)

fun Response.withMessage(error: AppError) = withMessage(error.toFlashMessage())

fun AppError.toFlashMessage() = FlashMessageDto(FlashMessageDto.Type.Error, messageCode)