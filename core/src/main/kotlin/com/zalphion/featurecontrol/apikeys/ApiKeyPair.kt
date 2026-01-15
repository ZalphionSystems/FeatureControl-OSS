package com.zalphion.featurecontrol.apikeys

import com.zalphion.featurecontrol.crypto.Base64String

class ApiKeyPair(
    val encrypted: Base64String,
    val hashed: Base64String
)