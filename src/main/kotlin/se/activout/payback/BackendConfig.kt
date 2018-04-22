package se.activout.payback

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import se.activout.kronslott.auth.session.SessionAuthConfig
import se.activout.kronslott.auth.session.SessionSettings
import se.activout.kronslott.objectmapper.ObjectMapperConfig
import se.activout.kronslott.objectmapper.ObjectMapperSettings
import se.activout.payback.accounts.backend.AccountApiSettings
import se.activout.payback.oauth2.frontend.OAuth2Config
import se.activout.payback.oauth2.frontend.OAuth2Settings
import javax.validation.Valid

class BackendConfig : Configuration(), OAuth2Config, ObjectMapperConfig, SessionAuthConfig {
    @Valid
    @JsonProperty("oauth2")
    override var oAuth2Settings: OAuth2Settings = OAuth2Settings()

    @Valid
    @JsonProperty("objectMapper")
    override var objectMapperSettings: ObjectMapperSettings = ObjectMapperSettings()

    @Valid
    @JsonProperty("session")
    override val sessionSettings: SessionSettings = SessionSettings()

    @Valid
    @JsonProperty("accountApi")
    val accountApiSettings: AccountApiSettings = AccountApiSettings()
}