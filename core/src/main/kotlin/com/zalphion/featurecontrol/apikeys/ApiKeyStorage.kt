package com.zalphion.featurecontrol.apikeys

import com.zalphion.featurecontrol.crypto.Base64String
import com.zalphion.featurecontrol.auth.EnginePrincipal

interface ApiKeyStorage {
    operator fun get(hashedApiKey: Base64String): EnginePrincipal?
    operator fun get(enginePrincipal: EnginePrincipal): Base64String?
    operator fun set(enginePrincipal: EnginePrincipal, pair: ApiKeyPair)

    companion object
}