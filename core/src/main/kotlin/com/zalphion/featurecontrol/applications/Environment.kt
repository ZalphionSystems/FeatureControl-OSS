package com.zalphion.featurecontrol.applications

import com.zalphion.featurecontrol.features.EnvironmentName
import com.zalphion.featurecontrol.lib.Colour

data class Environment(
    val name: EnvironmentName,
    val description: String,
    val colour: Colour,
    val extensions: Map<String, String>
)