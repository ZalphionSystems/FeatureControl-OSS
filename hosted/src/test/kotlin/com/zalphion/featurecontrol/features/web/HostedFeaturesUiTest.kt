package com.zalphion.featurecontrol.features.web

import com.zalphion.featurecontrol.CoreStorage
import com.zalphion.featurecontrol.JdbcCleanupExtension
import com.zalphion.featurecontrol.TestDataSource
import com.zalphion.featurecontrol.jdbc.jdbc
import org.junit.jupiter.api.extension.RegisterExtension

class HostedFeaturesUiTest: FeaturesUiTest({ CoreStorage.jdbc(it, TestDataSource.dataSource) }) {

    @RegisterExtension
    val cleanup = JdbcCleanupExtension(TestDataSource.dataSource)
}