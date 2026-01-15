package com.zalphion.featurecontrol.applications

import com.zalphion.featurecontrol.CoreStorage
import com.zalphion.featurecontrol.JdbcCleanupExtension
import com.zalphion.featurecontrol.TestDataSource
import com.zalphion.featurecontrol.jdbc.jdbc
import org.junit.jupiter.api.extension.RegisterExtension

class JdbcApplicationServiceTest: ApplicationServiceContract({CoreStorage.jdbc(it, TestDataSource.dataSource)}) {

    @RegisterExtension
    val cleaner = JdbcCleanupExtension(TestDataSource.dataSource)
}