package com.zalphion.featurecontrol

import com.zalphion.featurecontrol.apikeys.ApiKeyStorage
import com.zalphion.featurecontrol.configs.ConfigStorage
import com.zalphion.featurecontrol.applications.ApplicationStorage
import com.zalphion.featurecontrol.features.FeatureStorage
import com.zalphion.featurecontrol.members.MemberStorage
import com.zalphion.featurecontrol.teams.TeamStorage
import com.zalphion.featurecontrol.users.UserStorage

data class CoreStorage(
    val features: FeatureStorage,
    val applications: ApplicationStorage,
    val apiKeys: ApiKeyStorage,
    val users: UserStorage,
    val teams: TeamStorage,
    val members: MemberStorage,
    val configs: ConfigStorage
) {
    companion object
}