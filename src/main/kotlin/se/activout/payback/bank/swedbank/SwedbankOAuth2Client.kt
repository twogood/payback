package se.activout.payback.bank.swedbank

import mu.KotlinLogging
import org.glassfish.jersey.logging.LoggingFeature
import se.activout.payback.div
import se.activout.payback.oauth2.domain.OAuth2Client
import se.activout.payback.oauth2.domain.OAuth2Token
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

private val logger = KotlinLogging.logger {}


class SwedbankOAuth2Client @Inject constructor(private val url: String) : OAuth2Client {
    override fun get(oAuth2Token: OAuth2Token, path: String): Response {
        val client = ClientBuilder.newClient()
        client.register(LoggingFeature(Logger.getLogger("CLIENT"), Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, LoggingFeature.DEFAULT_MAX_ENTITY_SIZE));
        val webTarget = client.target(url) / path

        val response = webTarget
                .queryParam("BIC", "SANDSESS")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", oAuth2Token.authorizationHeaderValue)
                .header("Date", Date())
                .header("Request-ID", "foo")
                .header("Process-ID", "bar")
                .get()
        logger.debug { response }
        return response
    }
}