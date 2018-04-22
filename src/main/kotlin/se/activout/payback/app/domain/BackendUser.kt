package se.activout.payback.app.domain

import se.activout.payback.oauth2.domain.OAuth2Token
import java.security.Principal

class BackendUser(private val name: String, val oAuth2Token: OAuth2Token) : Principal {
    override fun getName(): String {
        return name
    }
}