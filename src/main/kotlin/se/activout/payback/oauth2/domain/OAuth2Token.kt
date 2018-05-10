package se.activout.payback.oauth2.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Strings.isNullOrEmpty

@JsonIgnoreProperties(ignoreUnknown = true)
class OAuth2Token(
        @JsonProperty("error") val error: String? = "",
        @JsonProperty("token_type") val tokenType: String? = "",
        @JsonProperty("access_token") val accessToken: String? = "",
        @JsonProperty("expires_in") val expiresIn: Int = 0,
        @JsonProperty("consented_on") val consentedOn: Int = 0,
        @JsonProperty("scope") val scope: String? = "",
        @JsonProperty("refresh_token") val refreshToken: String? = "",
        @JsonProperty("refresh_token_expires_in") val refreshTokenExpiresIn: Int = 0) {

    val success: Boolean
        @JsonIgnore
        get() = !hasError

    val hasError: Boolean
        @JsonIgnore
        get() = !isNullOrEmpty(error)

    val authorizationHeaderValue: String
        @JsonIgnore
        get() = "Bearer ${accessToken}"
}
