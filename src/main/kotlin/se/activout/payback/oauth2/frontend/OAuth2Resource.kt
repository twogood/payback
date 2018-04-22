package se.activout.payback.oauth2.frontend

import mu.KotlinLogging
import se.activout.payback.session.domain.SessionService
import se.activout.payback.oauth2.domain.OAuth2Service
import java.net.URI
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

interface OAuth2Config {
    var oAuth2Settings: OAuth2Settings
}

data class OAuth2Settings(val oauth2Url: String = "", val clientId: String = "", val clientSecret: String = "", val redirectUrl: String = "")

const val SCOPE_ACCOUNT_INFORMATION = "accountinformation"

private const val STATE_COOKIE_NAME = "state"

private val logger = KotlinLogging.logger {}

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class OAuth2Resource @Inject constructor(private val oAuth2Service: OAuth2Service, private val sessionService: SessionService) {

    @Path("start")
    @GET
    fun start(): Response {
        val state = oAuth2Service.generateState()
        val uri = oAuth2Service.createRedirectUri(state, SCOPE_ACCOUNT_INFORMATION)

        return Response
                .temporaryRedirect(uri)
                .cookie(oAuth2Service.prepareStateCookie(STATE_COOKIE_NAME, state))
                .build()
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

        val token = oAuth2Service.getToken(code)

        if (token.hasError) throw WebApplicationException(token.error, Response.Status.BAD_REQUEST)

        sessionService.createSession(httpServletRequest, token)

        return Response.temporaryRedirect(URI.create("/api/accounts")).build()
    }

}
