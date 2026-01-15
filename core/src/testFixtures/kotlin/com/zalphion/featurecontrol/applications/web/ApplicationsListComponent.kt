package com.zalphion.featurecontrol.applications.web

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.AriaRole
import com.zalphion.featurecontrol.applications.AppName
import com.zalphion.featurecontrol.web.getElement
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.zalphion.featurecontrol.web.getModal

open class ApplicationsListComponent(private val section: Locator): Iterable<AppName> {

    init {
        assertThat(section.getByRole(AriaRole.HEADING, Locator.GetByRoleOptions().setLevel(2))).isVisible()
    }

    fun select(appName: AppName, block: (ApplicationUi) -> Unit = {}): ApplicationUi {
        section.getElement(AriaRole.LINK, appName.value).click()
        return ApplicationUi(section.page()).also(block)
    }

    fun new(block: (ApplicationCreateUpdateUi) -> Unit): ApplicationCreateUpdateUi {
        section.getElement(AriaRole.BUTTON, "New Application").click()

        val modal = section.page().getModal("New Application")

        return ApplicationCreateUpdateUi.create(modal).also(block)
    }

    override fun iterator() = section
        .getByRole(AriaRole.LINK).all()
        .map { it.getByRole(AriaRole.HEADING).textContent() }
        .map { AppName.parse(it.trim()) }
        .iterator()

    val selected = section
        .locator("a[aria-current=page]")
        .getByRole(AriaRole.HEADING)
        .all()
        .firstOrNull()
        ?.let { AppName.parse(it.textContent().trim()) }
}

fun Page.applicationsList(): ApplicationsListComponent {
    val section = getByRole(AriaRole.COMPLEMENTARY, Page.GetByRoleOptions().setName("Applications Bar"))
    return ApplicationsListComponent(section)
}