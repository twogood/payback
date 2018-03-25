package se.activout.payback

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import se.activout.payback.auth.AuthConfig
import se.activout.payback.auth.AuthSettings

class BackendConfig : Configuration(), AuthConfig {
    @JsonProperty("auth")
    override var authSettings: AuthSettings = AuthSettings()
}