package com.zalphion.featurecontrol.configs

import com.zalphion.featurecontrol.core.ConfigsQueries
import com.zalphion.featurecontrol.features.EnvironmentName
import com.zalphion.featurecontrol.applications.AppId
import se.ansman.kotshi.JsonSerializable

fun ConfigStorage.Companion.jdbc(
    queries: ConfigsQueries
) = object: ConfigStorage {

    override fun get(appId: AppId): ConfigSpec? {
        val item = queries.getConfig(appId)
            .executeAsOneOrNull()
            ?: return null

       return ConfigSpec(
            appId = item.app_id,
            properties = item.properties.associate { it.toModel() }
        )
    }

    override fun get(appId: AppId, environmentName: EnvironmentName): ConfigEnvironment? {
        val item = queries.getValues(appId, environmentName)
            .executeAsOneOrNull()
            ?: return null

        return ConfigEnvironment(
            appId = item.app_id,
            environmentName = item.environment_name,
            values = item.values.associate { it.toModel() }
        )
    }

    override fun plusAssign(config: ConfigSpec) {
        queries.upsertConfig(
            appId = config.appId,
            properties = config.properties
                .map { it.toJdbc() }
                .toTypedArray()
        )
    }

    override fun plusAssign(environment: ConfigEnvironment) {
        queries.upsertValues(
            appId = environment.appId,
            environmentName = environment.environmentName,
            values = environment.values
                .map { it.toJdbc() }
                .toTypedArray()
        )
    }

    override fun minusAssign(appId: AppId) {
        queries.deleteConfig(appId)
    }

    override fun delete(appId: AppId, environmentName: EnvironmentName) {
        queries.deleteConfigValues(appId, environmentName)
    }
}

@JsonSerializable
data class JdbcProperty(
    val key: PropertyKey,
    val description: String,
    val group: String?,
    val type: JdbcPropertyType
)

@JsonSerializable
enum class JdbcPropertyType { Boolean, Decimal, String, Secret }

private fun JdbcPropertyType.toModel() = when(this) {
    JdbcPropertyType.Boolean -> PropertyType.Boolean
    JdbcPropertyType.Decimal -> PropertyType.Number
    JdbcPropertyType.String -> PropertyType.String
    JdbcPropertyType.Secret -> PropertyType.Secret
}

private fun PropertyType.toJdbc() = when(this) {
    PropertyType.Boolean -> JdbcPropertyType.Boolean
    PropertyType.Number -> JdbcPropertyType.Decimal
    PropertyType.String -> JdbcPropertyType.String
    PropertyType.Secret -> JdbcPropertyType.Secret
}

private fun JdbcProperty.toModel() = key to Property(
    description = description,
    group = group,
    type = type.toModel()
)

private fun Map.Entry<PropertyKey, Property>.toJdbc() = JdbcProperty(
    key = key,
    description = value.description,
    group = value.group,
    type = value.type.toJdbc()
)

@JsonSerializable
data class JdbcPropertyValue(val key: PropertyKey, val value: String, val type: JdbcPropertyType)

fun Map.Entry<PropertyKey, PropertyValue>.toJdbc() = JdbcPropertyValue(
    key = key,
    value = value.value,
    type = value.type.toJdbc()
)

fun JdbcPropertyValue.toModel() = key to PropertyValue(
    type = type.toModel(),
    value = value
)