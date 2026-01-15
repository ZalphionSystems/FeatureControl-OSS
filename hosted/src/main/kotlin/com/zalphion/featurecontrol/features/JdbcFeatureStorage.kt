package com.zalphion.featurecontrol.features

import com.zalphion.featurecontrol.core.Features
import com.zalphion.featurecontrol.core.FeaturesQueries
import com.zalphion.featurecontrol.applications.AppId
import com.zalphion.featurecontrol.jdbc.parseKeyValuePairs
import com.zalphion.featurecontrol.jdbc.toKeyValuePairs
import com.zalphion.featurecontrol.lib.toPage
import dev.andrewohara.utils.pagination.Paginator
import se.ansman.kotshi.JsonSerializable

fun FeatureStorage.Companion.jdbc(queries: FeaturesQueries) = object: FeatureStorage {
    override fun list(
        appId: AppId,
        pageSize: Int
    ) = Paginator<Feature, FeatureKey> { cursor ->
        queries.list(appId, cursor ?: FeatureKey.parse("00000000"), pageSize.plus(1).toLong())
            .executeAsList()
            .map { it.toFeature() }
            .toPage(pageSize, Feature::key)
    }

    override fun get(appId: AppId, featureKey: FeatureKey) = queries
        .get(appId, featureKey)
        .executeAsOneOrNull()
        ?.toFeature()

    override fun plusAssign(feature: Feature) {
        queries.upsert(
            app_id = feature.appId,
            feature_key = feature.key,
            environments = feature.environments.map { (key, value) ->
                JdbcFeatureEnvironment(
                    name = key,
                    weights = value.weights,
                    overrides = value.overrides,
                    extensions = value.extensions
                )
            }.toTypedArray(),
            default_variant = feature.defaultVariant,
            variants = feature.variants.toKeyValuePairs(),
            description = feature.description,
            extensions = feature.extensions
        )
    }

    override fun minusAssign(feature: Feature) {
        queries.delete(feature.appId, feature.key)
    }
}

private fun Features.toFeature() = Feature(
    appId = app_id,
    key = feature_key,
    variants = variants.parseKeyValuePairs(Variant),
    defaultVariant = default_variant,
    environments = environments.associate {
        it.name to FeatureEnvironment(
            weights = it.weights,
            overrides = it.overrides,
            extensions = it.extensions
        )
    },
    description = description,
    extensions = extensions
)

@JsonSerializable
data class JdbcFeatureEnvironment(
    val name: EnvironmentName,
    val weights: Map<Variant, Weight>,
    val overrides: Map<SubjectId, Variant>,
    val extensions: Map<String, String>
)