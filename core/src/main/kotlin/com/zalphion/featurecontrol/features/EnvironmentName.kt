package com.zalphion.featurecontrol.features

import dev.forkhandles.values.ComparableValue
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.and
import dev.forkhandles.values.maxLength
import dev.forkhandles.values.minLength
import dev.forkhandles.values.regex

class EnvironmentName private constructor(value: String): StringValue(value), ComparableValue<EnvironmentName, String> {
    companion object: StringValueFactory<EnvironmentName>(
        fn = ::EnvironmentName,
        validation = 2.minLength.and(32.maxLength).and("[a-zA-Z0-9-_]+".regex)
    )
}