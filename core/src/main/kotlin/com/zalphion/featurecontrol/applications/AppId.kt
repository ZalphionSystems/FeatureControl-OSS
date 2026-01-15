package com.zalphion.featurecontrol.applications

import com.zalphion.featurecontrol.ResourceIdValueFactory
import dev.forkhandles.values.ComparableValue
import dev.forkhandles.values.StringValue

class AppId private constructor(value: String): StringValue(value), ComparableValue<AppId, String> {
    companion object: ResourceIdValueFactory<AppId>(::AppId)
}