package com.zalphion.featurecontrol.applications.web

import com.zalphion.featurecontrol.CoreStorage
import com.zalphion.featurecontrol.JdbcCleanupExtension
import com.zalphion.featurecontrol.TestDataSource
import com.zalphion.featurecontrol.jdbc.jdbc
import org.junit.jupiter.api.extension.RegisterExtension

class HostedApplicationUiTest: ApplicationUiTest({ CoreStorage.jdbc(it, TestDataSource.dataSource) }) {

    @RegisterExtension
    val cleanup = JdbcCleanupExtension(TestDataSource.dataSource)
}