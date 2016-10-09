package ru.wutiarn.edustor.android.data.models

import com.fasterxml.jackson.annotation.JsonProperty

open class OAuthTokenResult {
    open lateinit var token: String
    @JsonProperty("expires_in") open var expiresIn: Long = 0
    open var scope: String? = null
    @JsonProperty("refresh_token") open var refreshToken: String? = null
}