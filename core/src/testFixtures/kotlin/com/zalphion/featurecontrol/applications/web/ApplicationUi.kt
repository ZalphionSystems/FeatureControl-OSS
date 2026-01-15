package com.zalphion.featurecontrol.applications.web

import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.zalphion.featurecontrol.applications.AppId

private val urlRegex = ".*/applications/([^/]+)/config.*".toRegex()

class ApplicationUi(private val page: Page) {

    init {
        assertThat(page).hasURL(urlRegex.toPattern())
    }

    val appId get() = AppId.parse(urlRegex.find(page.url())!!.groupValues[1])

    val applications get() = page.applicationsList()
    val application get() = page.application()
}
