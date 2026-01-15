package com.zalphion.featurecontrol.users

import dev.forkhandles.result4k.asResultOr

interface UserStorage {
    operator fun get(userId: UserId): User?
    operator fun get(userIds: Collection<UserId>): Collection<User>
    operator fun get(emailAddress: EmailAddress): User?
    operator fun plusAssign(user: User)

    fun getOrFail(emailAddress: EmailAddress) =
        get(emailAddress).asResultOr { userNotFoundByEmail(emailAddress) }

    fun getOrFail(userId: UserId) =
        get(userId).asResultOr { userNotFound(userId) }

    companion object
}