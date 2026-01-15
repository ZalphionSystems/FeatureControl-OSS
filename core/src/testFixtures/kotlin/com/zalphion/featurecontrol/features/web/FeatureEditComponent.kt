package com.zalphion.featurecontrol.features.web

import com.microsoft.playwright.Locator
import com.microsoft.playwright.assertions.PlaywrightAssertions
import com.microsoft.playwright.options.AriaRole
import com.zalphion.featurecontrol.features.Variant
import io.kotest.matchers.nulls.shouldNotBeNull

class FeatureEditComponent(private val section: Locator) {

    init {
        PlaywrightAssertions.assertThat(section).isVisible()
    }

    private val _description = section.getByLabel("Description")
        .first() // must specify first because there are duplicate inputs within the variant rows
    var description: String
        get() = _description.inputValue()
        set(value) { _description.fill(value) }

    val variants get() = section
        .locator("tbody tr").all()
        .map { VariantUI(it) }

    fun forVariant(name: Variant, block: (VariantUI) -> Unit = {}) = variants
        .find { it.name == name }
        .shouldNotBeNull()
        .also(block)

    fun forVariant(index: Int, block: (VariantUI) -> Unit = {}) = variants
        .getOrNull(index)
        .shouldNotBeNull()
        .also(block)

    fun newVariant(block: (VariantUI) -> Unit = {}): VariantUI {
        section.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("New Variant")).click()
        return variants.last().also(block)
    }
}

class VariantUI(private val section: Locator) {
    private val _name = section.getByRole(AriaRole.TEXTBOX, Locator.GetByRoleOptions().setName("Name"))
    var name: Variant
        get() = Variant.parse(_name.inputValue())
        set(value) { _name.fill(value.value) }

    private val _description = section.getByRole(AriaRole.TEXTBOX, Locator.GetByRoleOptions().setName("Description"))
    var description: String
        get() = _description.inputValue()
        set(value) { _description.fill(value) }

    private val _default = section.getByRole(AriaRole.RADIO, Locator.GetByRoleOptions().setName("Default"))
    var default: Boolean
        get() = _default.isChecked
        set(value) { if (value) _default.check() else _default.uncheck() }

    fun remove() {
        section.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Remove Variant")).click()
    }
}