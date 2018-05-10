package se.activout.payback.oauth2.domain

import mu.KotlinLogging
import org.glassfish.jersey.logging.LoggingFeature
import se.activout.payback.div
import se.activout.payback.domain.BankOAuth2Settings
import se.activout.payback.formOf
import se.activout.payback.readEntity
import java.net.URI
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.UriBuilder

private val logger = KotlinLogging.logger {}

class OAuth2Service @Inject constructor() {

    fun generateState(): String =
            randomString(32)

    fun createAuthorizeUri(state: String, settings: BankOAuth2Settings): URI {
        return UriBuilder.fromUri(settings.baseUrl).apply {
            path(settings.authorizePath)

            settings.authorizeParams.forEach { key, value -> queryParam(key, value) }
            queryParam("client_id", settings.clientId)
            queryParam("response_type", "code")
            queryParam("scope", settings.scopes.joinToString(" "))
            queryParam("redirect_uri", settings.redirectUrl)
            queryParam("state", state)
        }.build()
    }

    fun prepareStateCookie(name: String, state: String): NewCookie {
        val secure = false
        val maxAge = 300
        return NewCookie(name, state, "/", null, null, maxAge, secure, true)
    }

    fun getToken(code: String, settings: BankOAuth2Settings): OAuth2Token {
        val client = ClientBuilder.newClient()
        client.register(LoggingFeature(Logger.getLogger("CLIENT"), Level.FINE, LoggingFeature.Verbosity.PAYLOAD_ANY, LoggingFeature.DEFAULT_MAX_ENTITY_SIZE));
        val webTarget = client.target(settings.baseUrl) / settings.tokenPath

        val form = formOf(
                "grant_type" to "authorization_code",
                "code" to code,
                "client_id" to settings.clientId,
                "client_secret" to settings.clientSecret,
                "redirect_uri" to settings.redirectUrl
        )

        val response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED))
        logger.trace { response }

        val token: OAuth2Token = response.readEntity()
        logger.trace { token }

        return token
    }
}