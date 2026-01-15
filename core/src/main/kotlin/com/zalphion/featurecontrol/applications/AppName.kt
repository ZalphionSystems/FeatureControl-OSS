package com.zalphion.featurecontrol.applications

import dev.forkhandles.values.ComparableValue
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.maxLength

class AppName private constructor(value: String): StringValue(value), ComparableValue<AppName, String> {
    companion object: StringValueFactory<AppName>(::AppName, 128.maxLength)
}