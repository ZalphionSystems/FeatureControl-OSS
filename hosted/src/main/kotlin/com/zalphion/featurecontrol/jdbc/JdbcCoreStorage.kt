package com.zalphion.featurecontrol.jdbc

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zalphion.featurecontrol.CoreStorage
import com.zalphion.featurecontrol.ResourceIdValueFactory
import com.zalphion.featurecontrol.apikeys.ApiKeyStorage
import com.zalphion.featurecontrol.apikeys.jdbc
import com.zalphion.featurecontrol.configs.ConfigStorage
import com.zalphion.featurecontrol.configs.jdbc
import com.zalphion.featurecontrol.core.Applications
import com.zalphion.featurecontrol.core.Config_properties
import com.zalphion.featurecontrol.core.Config_values
import com.zalphion.featurecontrol.core.Features
import com.zalphion.featurecontrol.core.Members
import com.zalphion.featurecontrol.core.Teams
import com.zalphion.featurecontrol.core.Users
import com.zalphion.featurecontrol.features.EnvironmentName
import com.zalphion.featurecontrol.features.FeatureKey
import com.zalphion.featurecontrol.applications.ApplicationStorage
import com.zalphion.featurecontrol.features.FeatureStorage
import com.zalphion.featurecontrol.features.Variant
import com.zalphion.featurecontrol.features.jdbc
import com.zalphion.featurecontrol.members.MemberStorage
import com.zalphion.featurecontrol.members.jdbc
import com.zalphion.featurecontrol.members.UserRole
import com.zalphion.featurecontrol.applications.AppId
import com.zalphion.featurecontrol.applications.AppName
import com.zalphion.featurecontrol.applications.jdbc
import com.zalphion.featurecontrol.storage.CoreDatabase
import com.zalphion.featurecontrol.teams.TeamId
import com.zalphion.featurecontrol.teams.TeamName
import com.zalphion.featurecontrol.teams.TeamStorage
import com.zalphion.featurecontrol.teams.jdbc
import com.zalphion.featurecontrol.users.EmailAddress
import com.zalphion.featurecontrol.users.UserId
import com.zalphion.featurecontrol.users.UserStorage
import com.zalphion.featurecontrol.users.jdbc
import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import org.http4k.format.ConfigurableMoshi
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.sql.DataSource

fun <V: Value<PRIM>, PRIM: Any> values4kAdapter(vf: ValueFactory<V, PRIM>) = object: ColumnAdapter<V, PRIM> {
    override fun decode(databaseValue: PRIM) = vf.of(databaseValue)
    override fun encode(value: V) = vf.unwrap(value)
}

inline fun <reified T: Any> jsonAdapter(json: ConfigurableMoshi) = object: ColumnAdapter<T, String> {
    private val mapping = json.asBiDiMapping<T>()
    override fun decode(databaseValue: String) = mapping(databaseValue)
    override fun encode(value: T) = mapping(value)
}

val instantAdapter = object: ColumnAdapter<Instant, OffsetDateTime> {
    override fun decode(databaseValue: OffsetDateTime) = databaseValue.toInstant()
    override fun encode(value: Instant) = value.atOffset(ZoneOffset.UTC)
}

fun CoreStorage.Companion.jdbc(json: ConfigurableMoshi, dataSource: DataSource): CoreStorage {
    dataSource.migrate("core", "classpath:com/zalphion/featurecontrol/core")
    val database = CoreDatabase(
        driver = dataSource.asJdbcDriver(),
        applicationsAdapter = Applications.Adapter(
            team_idAdapter = values4kAdapter(TeamId),
            app_idAdapter = values4kAdapter(AppId),
            app_nameAdapter = values4kAdapter(AppName),
            extensionsAdapter = jsonAdapter(json),
            environmentsAdapter = jsonAdapter(json)
        ),
        usersAdapter = Users.Adapter(
            user_idAdapter = values4kAdapter(UserId),
            email_addressAdapter = values4kAdapter(EmailAddress)
        ),
        featuresAdapter = Features.Adapter(
            app_idAdapter = values4kAdapter(AppId),
            feature_keyAdapter = values4kAdapter(FeatureKey),
            default_variantAdapter = values4kAdapter(Variant),
            extensionsAdapter = jsonAdapter(json),
            environmentsAdapter = jsonAdapter(json)
        ),
        teamsAdapter = Teams.Adapter(
            team_idAdapter = values4kAdapter(TeamId),
            team_nameAdapter = values4kAdapter(TeamName)
        ),
        membersAdapter = Members.Adapter(
            team_idAdapter = values4kAdapter(TeamId),
            user_idAdapter = values4kAdapter(UserId),
            roleAdapter = EnumColumnAdapter<UserRole>(),
            invited_byAdapter = values4kAdapter(UserId)
        ),
        config_propertiesAdapter = Config_properties.Adapter(
            app_idAdapter = values4kAdapter(AppId),
            propertiesAdapter = jsonAdapter(json)
        ),
        config_valuesAdapter = Config_values.Adapter(
            app_idAdapter = values4kAdapter(AppId),
            environment_nameAdapter = values4kAdapter(EnvironmentName),
            valuesAdapter = jsonAdapter(json)
        )
    )

    return CoreStorage(
        teams = TeamStorage.jdbc(database.teamsQueries),
        applications = ApplicationStorage.jdbc(database.applicationsQueries),
        features = FeatureStorage.jdbc(database.featuresQueries),
        apiKeys = ApiKeyStorage.jdbc(dataSource),
        users = UserStorage.jdbc(database.usersQueries),
        members = MemberStorage.jdbc(database.membersQueries),
        configs = ConfigStorage.jdbc(database.configsQueries)
    )
}

fun <V: Value<String>> ResourceIdValueFactory<V>.min(): V = parse("00000000")