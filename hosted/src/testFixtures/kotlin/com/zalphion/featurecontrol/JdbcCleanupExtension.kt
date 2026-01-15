package com.zalphion.featurecontrol

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.sql.Connection
import javax.sql.DataSource

class JdbcCleanupExtension(
    private val dataSource: DataSource,
    private vararg val schemas: String = arrayOf("core")
): AfterEachCallback {

    private fun Connection.getAllTables(schema: String) = buildList {
        val rs = metaData.getTables(null, schema, "%", arrayOf("TABLE"))
        while (rs.next()) {
            add(rs.getString("TABLE_NAME"))
        }
    }.filter { it != "flyway_schema_history" }


    override fun afterEach(context: ExtensionContext) {
        dataSource.connection.use { connection ->
            for (schema in schemas) {
                val tables = connection.getAllTables(schema)
                connection.createStatement().use { statement ->
                    statement.execute("SET session_replication_role = replica")
                    tables.forEach { table -> statement.execute("TRUNCATE TABLE $schema.$table CASCADE") }
                    statement.execute("SET session_replication_role = DEFAULT")
                }
            }
        }
    }
}