package com.zalphion.featurecontrol

import com.zalphion.featurecontrol.auth.Entitlements
import com.zalphion.featurecontrol.crypto.AppSecret
import com.zalphion.featurecontrol.events.localEventBus
import com.zalphion.featurecontrol.plugins.Plugin
import com.zalphion.featurecontrol.plugins.PluginFactory
import com.zalphion.featurecontrol.teams.TeamId
import com.zalphion.featurecontrol.users.UserService
import org.http4k.core.Uri
import org.http4k.format.ConfigurableMoshi
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.collections.orEmpty
import kotlin.random.Random

abstract class CoreTestDriver(
    coreStorageFn: (ConfigurableMoshi) -> CoreStorage,
    pageSize: Int = 2,
    plugins: List<PluginFactory<*>> = emptyList(),
    private val appSecret: AppSecret = AppSecret.of("secret")
): Plugin {
    var time: Instant = Instant.parse("2025-07-29T12:00:00Z")
    private val clock get() = object: Clock() {
        override fun getZone() = ZoneOffset.UTC
        override fun withZone(zone: ZoneId?) = throw NotImplementedError()
        override fun instant() = time
    }
    val random = Random(1337)

    var entitlements = mutableMapOf<TeamId, Entitlements>()
    override fun getEntitlements(team: TeamId) = entitlements[team].orEmpty()

    val core = CoreBuilder(
        storageFn = coreStorageFn,
        clock = clock,
        random = random,
        staticUri = Uri.of("/"),
        origin = Uri.of("http://fake"),
        appSecret = appSecret,
        plugins = plugins + object: PluginFactory<CoreTestDriver>() {
            override fun createInternal(core: Core) = this@CoreTestDriver
        },
        eventBusFn = ::localEventBus
    ).build {
        config = config.copy(
            pageSize = pageSize,
            invitationRetention = Duration.ofDays(1),
        )
    }

    val users get() = UserService(core)
}