package se.activout.payback.bank.nordea

import mu.KotlinLogging
import org.glassfish.jersey.logging.LoggingFeature
import se.activout.payback.div
import se.activout.payback.oauth2.domain.OAuth2Client
import se.activout.payback.oauth2.domain.OAuth2Token
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

private val logger = KotlinLogging.logger {}


class NordeaOAuth2Client @Inject constructor(private val nordeaSettings: NordeaSettings) : OAuth2Client {
    override fun get(oAuth2Token: OAuth2Token, path: String): Response {
        val client = ClientBuilder.newClient()
        client.register(LoggingFeature(Logger.getLogger("CLIENT"), Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, LoggingFeature.DEFAULT_MAX_ENTITY_SIZE));
        val webTarget = client.target(nordeaSettings.accountApiUrl) / path

        val response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("X-IBM-Client-Id", nordeaSettings.oauth2.clientId)
                .header("X-IBM-Client-Secret", nordeaSettings.oauth2.clientSecret)
                .header("Authorization", oAuth2Token.authorizationHeaderValue)
                .get()
        logger.debug { response }
        return response
    }
}