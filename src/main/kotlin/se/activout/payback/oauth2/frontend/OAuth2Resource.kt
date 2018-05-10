package se.activout.payback.oauth2.frontend

import mu.KotlinLogging
import se.activout.payback.domain.BankLogin
import se.activout.payback.domain.BankName
import se.activout.payback.domain.BankOAuth2
import se.activout.payback.domain.BankService
import se.activout.payback.oauth2.domain.OAuth2Service
import se.activout.payback.session.domain.SessionService
import java.net.URI
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


private const val STATE_COOKIE_NAME = "state"

private val logger = KotlinLogging.logger {}

private inline fun checkAccess(value: Boolean, lazyMessage: () -> Any?): Unit {
    if (!value) {
        val message = lazyMessage()
        logger.warn { message }
        throw WebApplicationException(message.toString(), Response.Status.FORBIDDEN)
    }
}


@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class OAuth2Resource @Inject constructor(
        private val bankService: BankService,
        private val oAuth2Service: OAuth2Service,
        private val sessionService: SessionService
) {

    @GET
    @Path("{bankName}/login")
    fun login(
            @Context httpServletRequest: HttpServletRequest,
            @Valid @NotNull @PathParam("bankName") bankName: String): Response {
        val bank = bankService.getBank(BankName(bankName)) ?: throw WebApplicationException(Response.Status.NOT_FOUND)

        val bankLogin = object : BankLogin {
            lateinit var response: Response

            override fun oAuth2(bankOAuth2: BankOAuth2) {
                sessionService.setAttribute(httpServletRequest,
                        sessionAttributeKey(bank.name.value),
                        bankOAuth2
                )

                val state = oAuth2Service.generateState()
                val uri = oAuth2Service.createAuthorizeUri(state, bankOAuth2.settings)

                response = Response
                        .temporaryRedirect(uri)
                        .cookie(oAuth2Service.prepareStateCookie(STATE_COOKIE_NAME, state))
                        .cookie(SessionHelper.getSessionCookie(httpServletRequest))
                        .build()
            }
        }

        bank.login(bankLogin)
        return bankLogin.response
    }
    /*
    @Path("start")
    @GET
    fun start(): Response {
        val state = oAuth2Service.generateState()
        val uri = oAuth2Service.createAuthorizeUri(state, SCOPE_ACCOUNT_INFORMATION)

        return Response
                .temporaryRedirect(uri)
                .cookie(oAuth2Service.prepareStateCookie(STATE_COOKIE_NAME, state))
                .build()
    }*/

    @Deprecated("Defaults to SEB", ReplaceWith("{bankName}/oauth2callback"))
    @Path("oauth2callback")
    @GET
    fun oauth2callbackOld(
            @Context httpServletRequest: HttpServletRequest,
            @QueryParam("error") error: String?,
            @QueryParam("code") code: String?,
            @NotNull @CookieParam(STATE_COOKIE_NAME) cookieState: String
    ): Response {
        logger.warn { "Using old oauth2callback" }
        return oauth2callback(httpServletRequest, "seb", error, code, null, cookieState)
    }

    @Path("{bankName}/oauth2callback")
    @GET
    fun oauth2callback(
            @Context httpServletRequest: HttpServletRequest,
            @Valid @NotNull @PathParam("bankName") bankName: String,
            @QueryParam("error") error: String?,
            @QueryParam("code") code: String?,
            @QueryParam("state") state: String?,
            @NotNull @CookieParam(STATE_COOKIE_NAME) cookieState: String
    ): Response {

        checkAccess(error != null) { error }
        checkAccess(code == null) { "code is null" }

        val bankOAuth2: BankOAuth2 = sessionService.getAndRemoveAttribute(httpServletRequest, sessionAttributeKey(bankName))
                ?: throw WebApplicationException("missing session attribute", Response.Status.FORBIDDEN)

        val settings = bankOAuth2.settings

        if (settings.checkState) {
            checkAccess(state == cookieState) { "state mismatch" }
        }

        val token = oAuth2Service.getToken(code!!, settings)

        checkAccess(token.success) { "token.error=${token.error}" }

        sessionService.createSession(httpServletRequest, BankName(bankName), bankOAuth2.createBankCredential(token))

        return Response.temporaryRedirect(URI.create("/api/accounts")).build()
    }

    private fun sessionAttributeKey(name: Any) = "OAuth2:$name"


}
