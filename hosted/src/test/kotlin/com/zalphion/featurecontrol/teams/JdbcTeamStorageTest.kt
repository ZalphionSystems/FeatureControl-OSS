package com.zalphion.featurecontrol.teams

import com.zalphion.featurecontrol.CoreStorage
import com.zalphion.featurecontrol.JdbcCleanupExtension
import com.zalphion.featurecontrol.TestDataSource
import com.zalphion.featurecontrol.jdbc.jdbc
import org.junit.jupiter.api.extension.RegisterExtension

class JdbcTeamStorageTest: TeamStorageContract({CoreStorage.jdbc(it, TestDataSource.dataSource)}) {
    @RegisterExtension
    val cleaner = JdbcCleanupExtension(TestDataSource.dataSource)
}