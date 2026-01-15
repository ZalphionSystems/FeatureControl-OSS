package com.zalphion.featurecontrol.applications.web

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole
import com.zalphion.featurecontrol.applications.AppName
import com.zalphion.featurecontrol.features.FeatureKey
import com.zalphion.featurecontrol.features.web.FeatureCreateUI
import com.zalphion.featurecontrol.features.web.FeatureUi
import com.zalphion.featurecontrol.web.getElement
import com.zalphion.featurecontrol.web.getModal
import io.kotest.matchers.nulls.shouldNotBeNull

class ApplicationComponent(private val section: Locator): Iterable<FeatureKey> {

    init {
        assertThat(section).isVisible()
    }

    val name get() = section.getByRole(AriaRole.HEADING, Locator.GetByRoleOptions().setLevel(2))
        .let { AppName.parse(it.textContent().trim()) }

    fun newFeature(block: (FeatureCreateUI) -> Unit): FeatureCreateUI {
        section.getElement(AriaRole.BUTTON, "New Feature").click()
        val modal = section.page().getModal("New Feature")
        return FeatureCreateUI(modal).also(block)
    }

    fun select(featureKey: FeatureKey, block: (FeatureUi) -> Unit = {}): FeatureUi {
        section
            .getByRole(AriaRole.LINK)
            .filter(Locator.FilterOptions().setHasText("Feature"))
            .all()
            .map { it.getByRole(AriaRole.HEADING) }
            .find { it.textContent().trim() == featureKey.value }
            .shouldNotBeNull()
            .click()

        return FeatureUi(section.page()).also(block)
    }

    override fun iterator() = section
        .getByRole(AriaRole.LINK)
        .filter(Locator.FilterOptions().setHasText("Feature"))
        .all()
        .map { it.getByRole(AriaRole.HEADING) }
        .map { FeatureKey.parse(it.textContent().trim()) }
        .iterator()

    fun more(block: (ApplicationMenuComponent) -> Unit = {}): ApplicationMenuComponent {
        section.getElement(AriaRole.BUTTON, "More Options").click()
        return ApplicationMenuComponent(section, name).also(block)
    }
}

fun Page.application() = getByRole(AriaRole.REGION, Page.GetByRoleOptions().setName("Application Details"))
    .let(::ApplicationComponent)
