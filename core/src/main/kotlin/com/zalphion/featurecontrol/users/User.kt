package com.zalphion.featurecontrol.users

import com.zalphion.featurecontrol.ResourceIdValueFactory
import dev.forkhandles.values.ComparableValue
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import java.net.URI

data class User(
    val userId: UserId,
    val emailAddress: EmailAddress,
    val userName: String?,
    val photoUrl: URI?
) {
    fun fullName() = if (userName.isNullOrBlank()) emailAddress.value else {
        "$userName ($emailAddress)"
    }
}

class UserId private constructor(value: String): StringValue(value), ComparableValue<UserId, String> {
    companion object: ResourceIdValueFactory<UserId>(::UserId)
}

class EmailAddress private constructor(value: String): StringValue(value), ComparableValue<EmailAddress, String> {
    companion object: StringValueFactory<EmailAddress>(::EmailAddress)
}