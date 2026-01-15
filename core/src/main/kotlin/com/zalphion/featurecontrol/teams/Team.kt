package com.zalphion.featurecontrol.teams

import dev.forkhandles.values.NonEmptyStringValueFactory
import dev.forkhandles.values.StringValue

data class Team(
    val teamId: TeamId,
    val teamName: TeamName
)

class TeamName private constructor(value: String): StringValue(value) {
    companion object: NonEmptyStringValueFactory<TeamName>(::TeamName)
}