package com.zalphion.featurecontrol.plugins

import com.zalphion.featurecontrol.Core
import com.zalphion.featurecontrol.JsonExport

abstract class PluginFactory<P: Plugin>(
    val jsonExport: JsonExport? = null,
    private val onCreate: (P) -> Unit = {},
) {
    protected abstract fun createInternal(core: Core): P

    fun create(core: Core) = createInternal(core).also(onCreate)
}