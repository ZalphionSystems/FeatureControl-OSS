package com.zalphion.featurecontrol.teams

import com.zalphion.featurecontrol.ResourceIdValueFactory
import dev.forkhandles.values.ComparableValue
import dev.forkhandles.values.StringValue

class TeamId private constructor(value: String): StringValue(value), ComparableValue<TeamId, String> {
    companion object: ResourceIdValueFactory<TeamId>(::TeamId)
}