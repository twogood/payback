package se.activout.payback

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import se.activout.kronslott.auth.session.SessionAuthConfig
import se.activout.kronslott.auth.session.SessionSettings
import se.activout.kronslott.objectmapper.ObjectMapperConfig
import se.activout.kronslott.objectmapper.ObjectMapperSettings
import se.activout.payback.bank.nordea.NordeaSettings
import se.activout.payback.bank.seb.SebSettings
import se.activout.payback.bank.swedbank.SwedbankSettings
import se.activout.payback.domain.BankOAuth2Settings
import javax.validation.Valid

data class OAuth2SettingsJson(
        val baseUrl: String? = null,
        val authorizePath: String? = null,
        val tokenPath: String? = null,
        val clientId: String? = null,
        val clientSecret: String? = null,
        val redirectUrl: String? = null,
        val scopes: List<String>? = null
) {
    fun toOAuth2Settings(): BankOAuth2Settings {
        return BankOAuth2Settings(
                baseUrl = checkNotNull(baseUrl),
                authorizePath = checkNotNull(authorizePath),
                tokenPath = checkNotNull(tokenPath),
                clientId = checkNotNull(clientId),
                clientSecret = checkNotNull(clientSecret),
                redirectUrl = checkNotNull(redirectUrl),
                scopes = checkNotNull(scopes)
        )
    }
}

data class BankSettingsJson(val accountApiUrl: String? = null, val oauth2: OAuth2SettingsJson? = null) {
    fun toSebSettings(): SebSettings {
        return SebSettings(
                accountApiUrl = checkNotNull(accountApiUrl),
                oauth2 = checkNotNull(oauth2).toOAuth2Settings()
        )
    }

    fun toSwedbankSettings(): SwedbankSettings {
        return SwedbankSettings(
                accountApiUrl = checkNotNull(accountApiUrl),
                oauth2 = checkNotNull(oauth2).toOAuth2Settings().copy(authorizeParams = mapOf("bic" to "SANDSESS"))
        )
    }

    fun toNordeaSettings(): NordeaSettings {
        return NordeaSettings(
                accountApiUrl = checkNotNull(accountApiUrl),
                oauth2 = checkNotNull(oauth2).toOAuth2Settings()
        )
    }
}

class BackendConfig : Configuration(), ObjectMapperConfig, SessionAuthConfig {

    @Valid
    @JsonProperty("objectMapper")
    override var objectMapperSettings: ObjectMapperSettings = ObjectMapperSettings()

    @Valid
    @JsonProperty("session")
    override val sessionSettings: SessionSettings = SessionSettings()

    @Valid
    @JsonProperty("nordea")
    val nordea: BankSettingsJson = BankSettingsJson()

    @Valid
    @JsonProperty("seb")
    val seb: BankSettingsJson = BankSettingsJson()

    @Valid
    @JsonProperty("swedbank")
    val swedbank: BankSettingsJson = BankSettingsJson()
}