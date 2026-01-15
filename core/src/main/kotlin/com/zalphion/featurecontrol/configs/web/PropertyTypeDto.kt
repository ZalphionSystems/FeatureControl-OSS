package com.zalphion.featurecontrol.configs.web

import com.zalphion.featurecontrol.configs.PropertyType
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
enum class PropertyTypeDto {
    String, // order matters for generating UI
    Number,
    Boolean,
    Secret
}

fun PropertyType.toDto() = when(this) {
    PropertyType.Boolean -> PropertyTypeDto.Boolean
    PropertyType.Number -> PropertyTypeDto.Number
    PropertyType.String -> PropertyTypeDto.String
    PropertyType.Secret -> PropertyTypeDto.Secret
}

fun PropertyTypeDto.toModel() = when(this) {
    PropertyTypeDto.Boolean -> PropertyType.Boolean
    PropertyTypeDto.Number -> PropertyType.Number
    PropertyTypeDto.String -> PropertyType.String
    PropertyTypeDto.Secret -> PropertyType.Secret
}