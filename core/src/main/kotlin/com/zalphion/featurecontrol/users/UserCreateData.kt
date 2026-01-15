package com.zalphion.featurecontrol.users

import java.net.URI
import kotlin.random.Random

data class UserCreateData(
    val emailAddress: EmailAddress,
    val userName: String?,
    val photoUrl: URI?,
)

fun UserCreateData.toUser(random: Random) = User(
    userId = UserId.random(random),
    emailAddress = emailAddress,
    userName = userName,
    photoUrl = photoUrl
)