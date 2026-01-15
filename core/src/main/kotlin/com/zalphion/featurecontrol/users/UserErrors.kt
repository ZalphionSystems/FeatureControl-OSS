package com.zalphion.featurecontrol.users

import com.zalphion.featurecontrol.AppError

fun userNotFound(userId: UserId) = AppError(
    messageCode = "userNotFound",
    messageArguments = mapOf(
        "userId" to userId.value
    )
)

fun userNotFoundByEmail(emailAddress: EmailAddress) = AppError(
    messageCode = "userNotFoundByEmail",
    messageArguments = mapOf(
        "emailAddress" to emailAddress.value
    )
)

fun userAlreadyExists(emailAddress: EmailAddress) = AppError(
    messageCode = "userAlreadyExists",
    messageArguments = mapOf(
        "emailAddress" to emailAddress.value
    )
)