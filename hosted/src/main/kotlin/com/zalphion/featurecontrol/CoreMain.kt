package com.zalphion.featurecontrol

import com.squareup.moshi.JsonAdapter
import com.zalphion.featurecontrol.web.APP_SLUG
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zalphion.featurecontrol.emails.EmailSender
import com.zalphion.featurecontrol.emails.email
import com.zalphion.featurecontrol.web.LOGIN_PATH
import com.zalphion.featurecontrol.emails.smtp
import com.zalphion.featurecontrol.events.localEventBus
import com.zalphion.featurecontrol.jdbc.jdbc
import com.zalphion.featurecontrol.plugins.Plugin
import org.http4k.config.Environment
import org.http4k.core.Credentials
import org.http4k.core.Uri
import org.http4k.core.query
import org.http4k.filter.ServerFilters
import org.http4k.server.Undertow
import org.http4k.server.asServer
import se.ansman.kotshi.KotshiJsonAdapterFactory
import java.security.SecureRandom
import java.time.Clock
import javax.sql.DataSource
import kotlin.random.asKotlinRandom

fun main() = hostedCoreMain()

@KotshiJsonAdapterFactory
private object HostedCoreJsonAdapterFactory: JsonAdapter.Factory by KotshiHostedCoreJsonAdapterFactory

fun hostedCoreMain(fn: CoreBuilder.(DataSource) -> Unit = {}) {
    val env = Environment.ENV

    val dataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = Uri(
            scheme = "jdbc:postgresql",
            host = env[Settings.postgresDatabaseHost].host.value,
            port = env[Settings.postgresDatabaseHost].port?.value,
            path = "/$APP_SLUG",
            query = "",
            userInfo = "",
            fragment = ""
        )
            .query("user", env[Settings.postgresDatabaseUsername])
            .query("password", env[Settings.postgresDatabasePassword].use { it })
            .query("ssl", "false")
            .toString()
    })

    val core = CoreBuilder(
        clock = Clock.systemUTC(),
        random = SecureRandom().asKotlinRandom(),
        appSecret = env[Settings.appSecret],
        storageFn = { json -> CoreStorage.jdbc(json, dataSource) },
        staticUri = Uri.of("/"), // vendored in jar
        origin = env[Settings.origin],
        eventBusFn = ::localEventBus
    ).build {
        plugins += HostedCoreJsonAdapterFactory.asJsonPlugin()
        plugins += Plugin.email(
            loginUri = env[Settings.origin].path(LOGIN_PATH),
            emails = EmailSender.smtp(
                fromName = env[Settings.smtpFromName],
                fromAddress = env[Settings.smtpFromAddress],
                authority = env[Settings.smtpAuthority],
                credentials = env[Settings.smtpUsername]?.let { username ->
                    Credentials(username, env[Settings.smtpPassword].use { it })
                },
                startTls = env[Settings.smtpStartTls],
            )
        )

        fn(this, dataSource)

        config = config.copy(
            googleClientId = env[Settings.googleClientId],
            csrfTtl = env[Settings.csrfTtl],
            sessionLength = env[Settings.sessionLength],
            invitationRetention = env[Settings.invitationsRetention],
            pageSize = env[Settings.pageSize]
        )
    }

    core.getRoutes()
        .withFilter(ServerFilters.GZip())
        .asServer(Undertow(env[Settings.port].value)) // TODO consider helidon
        .start()
        .also { println("Started on http://localhost:${it.port()}") }
        .block()
}