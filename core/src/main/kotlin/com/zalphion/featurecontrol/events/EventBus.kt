package com.zalphion.featurecontrol.events

import com.zalphion.featurecontrol.plugins.Plugin
import dev.forkhandles.result4k.peekFailure
import io.github.oshai.kotlinlogging.KotlinLogging

typealias EventBus = (Event) -> Unit

fun localEventBus(plugins: List<Plugin>): EventBus {
    val logger = KotlinLogging.logger { }
    return { event ->
        for (plugin in plugins) {
            try {
                plugin.onEvent(event).peekFailure {
                    logger.error { "Error handling $event: $it"}
                }
            } catch (e: Exception) {
                logger.error(e) { "Uncaught error handling $event"}
            }
        }
    }
}