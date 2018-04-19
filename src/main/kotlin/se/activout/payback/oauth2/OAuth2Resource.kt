package se.activout.payback.oauth2

import mu.KotlinLogging
import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.logging.LoggingFeature.DEFAULT_MAX_ENTITY_SIZE
import se.activout.kronslott.auth.session.SessionUserHelper
import se.activout.payback.BackendUser
import se.activout.payback.div
import se.activout.payback.formOf
import se.activout.payback.readEntity
import java.net.URI
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.core.*

interface OAuth2Config {
    var oAuth2Settings: OAuth2Settings
}

data class OAuth2Settings(val oauth2Url: String = "", val clientId: String = "", val clientSecret: String = "", val redirectUrl: String = "")

private const val STATE_COOKIE_NAME = "state"
private const val SCOPE_ACCOUNT_INFORMATION = "accountinformation"

private val logger = KotlinLogging.logger {}

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class OAuth2Resource @Inject constructor(private val settings: OAuth2Settings) {

    @Path("start")
    @GET
    fun start(): Response {
        val state = randomString(32)
        val uri = UriBuilder.fromUri(settings.oauth2Url + "/authorize")
                .queryParam("client_id", settings.clientId)
                //.queryParam("response_type", "token")
                .queryParam("response_type", "code")
                .queryParam("scope", SCOPE_ACCOUNT_INFORMATION)
                .queryParam("redirect_uri", settings.redirectUrl)
                .queryParam("state", state)
                .build()

        return Response
                .temporaryRedirect(uri)
                .cookie(stateCookie(state))
                .build()
    }

    private fun stateCookie(state: String): NewCookie {
        val secure = false
        val maxAge = 300
        return NewCookie(STATE_COOKIE_NAME, state, "/", null, null, maxAge, secure, true)
    }

    @Path("oauth2callback")
    @GET
    fun oauth2callback(
            @Context httpServletRequest: HttpServletRequest,
            @QueryParam("error") error: String?,
            @QueryParam("code") code: String?,
            @NotNull @CookieParam(STATE_COOKIE_NAME) cookieState: String
    ): Response {

        if (error != null) throw WebApplicationException(error, Response.Status.BAD_REQUEST)
        if (code == null) throw WebApplicationException(Response.Status.BAD_REQUEST)

        val client = ClientBuilder.newClient()
        client.register(LoggingFeature(Logger.getLogger("CLIENT"), Level.FINE, LoggingFeature.Verbosity.PAYLOAD_ANY, DEFAULT_MAX_ENTITY_SIZE));
        val webTarget = client.target(settings.oauth2Url) / "token"

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

        if (token.hasError) throw WebApplicationException(token.error, Response.Status.BAD_REQUEST)

        SessionUserHelper.createSession(httpServletRequest, BackendUser("user", token))

        return Response.temporaryRedirect(URI.create("/api/accounts")).build()
    }

}
