package com.zalphion.featurecontrol

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zalphion.featurecontrol.web.APP_SLUG
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.postgresql.PostgreSQLContainer

object TestDataSource {
    private val postgres: JdbcDatabaseContainer<*> = PostgreSQLContainer("postgres:15-alpine")
        .withDatabaseName(APP_SLUG)
        .withUsername("test")
        .withPassword("test")

    val dataSource by lazy {
        postgres.start()
        HikariConfig().apply {
            jdbcUrl = postgres.jdbcUrl
            schema = "core"
            username = postgres.username
            password = postgres.password
        }.let(::HikariDataSource)
    }
}