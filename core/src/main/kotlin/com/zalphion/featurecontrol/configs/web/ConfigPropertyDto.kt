package com.zalphion.featurecontrol.configs.web

import com.zalphion.featurecontrol.configs.Property
import com.zalphion.featurecontrol.configs.PropertyKey
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ConfigPropertyDto(
    val key: PropertyKey,
    val description: String = "",
    val group: String?,
    val type: PropertyTypeDto
)

fun Property.toDto(key: PropertyKey) = ConfigPropertyDto(
    key = key,
    description = description,
    group = group,
    type = type.toDto()
)

fun ConfigPropertyDto.toModel() = key to Property(
    description = description,
    group = group,
    type = type.toModel()
)