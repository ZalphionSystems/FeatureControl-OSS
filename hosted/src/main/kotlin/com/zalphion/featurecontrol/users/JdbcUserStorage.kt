package com.zalphion.featurecontrol.users

import com.zalphion.featurecontrol.core.Users
import com.zalphion.featurecontrol.core.UsersQueries
import java.net.URI

fun UserStorage.Companion.jdbc(queries: UsersQueries) = object: UserStorage {

    override fun get(userId: UserId) = queries
        .get(userId)
        .executeAsOneOrNull()
        ?.toUser()

    override fun get(userIds: Collection<UserId>): List<User> {
        if (userIds.isEmpty()) return emptyList()
        return queries
            .getAll(userIds)
            .executeAsList()
            .map { it.toUser() }
    }

    override fun get(emailAddress: EmailAddress) = queries
        .getByEmail(emailAddress)
        .executeAsOneOrNull()
        ?.toUser()

    override fun plusAssign(user: User) {
        queries.upsert(
            userId = user.userId,
            username = user.userName,
            emailAddress = user.emailAddress,
            photoUrl = user.photoUrl?.toString(),
        )
    }
}

private fun Users.toUser() = User(
    userId = user_id,
    userName = user_name,
    emailAddress = email_address,
    photoUrl = photo_url?.let(URI::create),
)