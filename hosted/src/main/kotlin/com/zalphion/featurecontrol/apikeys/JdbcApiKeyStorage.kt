package com.zalphion.featurecontrol.apikeys

import com.zalphion.featurecontrol.features.EnvironmentName
import com.zalphion.featurecontrol.applications.AppId
import com.zalphion.featurecontrol.crypto.Base64String
import com.zalphion.featurecontrol.auth.EnginePrincipal
import javax.sql.DataSource

private const val AUTHORIZE = """
SELECT app_id, environment_name
FROM api_keys
WHERE api_key_hashed_base64 = ?
"""

private const val GET = """
SELECT api_key_encrypted_base64
FROM api_keys
WHERE app_id = ?
AND environment_name = ?
"""

private const val CREATE = """
INSERT INTO api_keys
(app_id, environment_name, api_key_hashed_base64, api_key_encrypted_base64)
VALUES (?, ?, ?, ?)
"""

fun ApiKeyStorage.Companion.jdbc(dataSource: DataSource) = object: ApiKeyStorage {

    override fun get(hashedApiKey: Base64String) = dataSource.connection.use { conn ->
        conn.prepareStatement(AUTHORIZE).use { stmt ->
            stmt.setString(1, hashedApiKey.value)

            stmt.executeQuery().use { rs ->
                if (!rs.next()) null else EnginePrincipal(
                    appId = AppId.parse(rs.getString("app_id")),
                    environmentName = EnvironmentName.parse(rs.getString("environment_name"))
                )
            }
        }
    }

    override fun get(enginePrincipal: EnginePrincipal) = dataSource.connection.use { conn ->
        conn.prepareStatement(GET).use { stmt ->
            stmt.setString(1, enginePrincipal.appId.value)
            stmt.setString(2, enginePrincipal.environmentName.value)

            stmt.executeQuery().use { rs ->
                if (!rs.next()) null else {
                    Base64String.parse(rs.getString("api_key_encrypted_base64"))
                }
            }
        }
    }

    override fun set(enginePrincipal: EnginePrincipal, pair: ApiKeyPair) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(CREATE).use { stmt ->
                stmt.setString(1, enginePrincipal.appId.value)
                stmt.setString(2, enginePrincipal.environmentName.value)
                stmt.setString(3, pair.hashed.value)
                stmt.setString(4, pair.encrypted.value)

                stmt.execute()
            }
        }
    }
}