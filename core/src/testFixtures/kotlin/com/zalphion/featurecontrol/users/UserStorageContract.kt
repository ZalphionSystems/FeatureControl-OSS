package com.zalphion.featurecontrol.users

import com.zalphion.featurecontrol.CoreStorage
import com.zalphion.featurecontrol.CoreTestDriver
import com.zalphion.featurecontrol.idp1Email1
import com.zalphion.featurecontrol.idp1Email2
import com.zalphion.featurecontrol.idp1Email3
import com.zalphion.featurecontrol.idp2Email1
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.http4k.format.ConfigurableMoshi
import org.junit.jupiter.api.Test

abstract class UserStorageContract(
    storageFn: (ConfigurableMoshi) -> CoreStorage
): CoreTestDriver(storageFn) {

    private val user1 = User(UserId.random(random), idp1Email1, "user1", null)
        .also(core.users::plusAssign)
    private val user2 = User(UserId.random(random), idp1Email2, "user2", null)
        .also(core.users::plusAssign)
    private val user3 = User(UserId.random(random), idp1Email3, "user3", null)
        .also(core.users::plusAssign)
    private val user4 = User(UserId.random(random), idp2Email1,"user4", null)
        .also(core.users::plusAssign)

    @Test
    fun `get user - found`() {
        core.users[user1.userId] shouldBe user1
    }

    @Test
    fun `get user - not found`() {
        core.users[UserId.random(random)].shouldBeNull()
    }

    @Test
    fun `get user by email - found`() {
        core.users[user4.emailAddress] shouldBe user4
    }

    @Test
    fun `get user by email - not found`() {
        core.users[EmailAddress.of("not@found.com")].shouldBeNull()
    }

    @Test
    fun `update user`() {
        core.users += user2.copy(
            userName = "user1",
        )
        core.users[user2.userId] shouldBe user2.copy(
            userName = "user1",
        )
    }
}