package com.zalphion.featurecontrol.jdbc

import org.flywaydb.core.Flyway
import javax.sql.DataSource

fun DataSource.migrate(schema: String, locations: String) = Flyway
    .configure()
    .dataSource(this)
    .schemas(schema)
    .createSchemas(true)
    .locations(locations)
    .load()
    .migrate()
    .let {  }