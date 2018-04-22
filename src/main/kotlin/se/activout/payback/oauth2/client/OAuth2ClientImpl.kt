package se.activout.payback.oauth2.client

import mu.KotlinLogging
import org.glassfish.jersey.logging.LoggingFeature
import se.activout.payback.div
import se.activout.payback.oauth2.domain.OAuth2Client
import se.activout.payback.oauth2.domain.OAuth2Token
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Invocation
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

private val logger = KotlinLogging.logger {}

private fun Invocation.Builder.authorizationHeader(oAuth2Token: OAuth2Token): Invocation.Builder {
    return header("Authorization", oAuth2Token.authorizationHeaderValue)
}

class OAuth2ClientImpl @Inject constructor(private val url: String) : OAuth2Client {
    override fun get(oAuth2Token: OAuth2Token, path: String): Response {
        val client = ClientBuilder.newClient()
        client.register(LoggingFeature(Logger.getLogger("CLIENT"), Level.FINE, LoggingFeature.Verbosity.PAYLOAD_ANY, LoggingFeature.DEFAULT_MAX_ENTITY_SIZE));
        val webTarget = client.target(url) / path

        val response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .authorizationHeader(oAuth2Token)
                .get()
        logger.debug { response }
        return response
    }
}