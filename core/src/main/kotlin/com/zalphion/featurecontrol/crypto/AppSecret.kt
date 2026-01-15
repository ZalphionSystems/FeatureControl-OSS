package com.zalphion.featurecontrol.crypto

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength

class AppSecret private constructor(value: String): StringValue(value) {
    companion object: StringValueFactory<AppSecret>(::AppSecret)
}