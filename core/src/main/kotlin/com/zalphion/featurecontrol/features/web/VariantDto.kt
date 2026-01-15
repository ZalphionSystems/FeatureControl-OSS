package com.zalphion.featurecontrol.features.web

import com.zalphion.featurecontrol.features.Variant
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class VariantDto(
    val name: Variant,
    val description: String = ""
)