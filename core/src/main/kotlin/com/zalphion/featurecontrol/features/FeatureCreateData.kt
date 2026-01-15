package com.zalphion.featurecontrol.features

import com.zalphion.featurecontrol.plugins.Extensions
import com.zalphion.featurecontrol.applications.AppId

data class FeatureCreateData(
    val featureKey: FeatureKey,
    val variants: Map<Variant, String>,
    val defaultVariant: Variant,
    val environments: Map<EnvironmentName, FeatureEnvironment>,
    val description: String,
    val extensions: Extensions
)

fun FeatureCreateData.toFeature(appId: AppId) = Feature(
    appId = appId,
    key = featureKey,
    variants = variants,
    defaultVariant = defaultVariant,
    environments = environments,
    description = description,
    extensions = extensions
)