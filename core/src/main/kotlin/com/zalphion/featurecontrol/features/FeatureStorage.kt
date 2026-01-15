package com.zalphion.featurecontrol.features

import com.zalphion.featurecontrol.applications.AppId
import com.zalphion.featurecontrol.featureNotFound
import dev.andrewohara.utils.pagination.Paginator
import dev.forkhandles.result4k.asResultOr

interface FeatureStorage {
    fun list(appId: AppId, pageSize: Int): Paginator<Feature, FeatureKey>
    operator fun get(appId: AppId, featureKey: FeatureKey): Feature?
    operator fun plusAssign(feature: Feature)
    operator fun minusAssign(feature: Feature)

    fun getOrFail(appId: AppId, featureKey: FeatureKey) =
        get(appId, featureKey).asResultOr { featureNotFound(appId, featureKey) }

    companion object
}