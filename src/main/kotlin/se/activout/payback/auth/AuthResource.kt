package se.activout.payback.auth

import java.util.concurrent.ThreadLocalRandom
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder

interface AuthConfig {
    var authSettings: AuthSettings
}

data class AuthSettings(val authorizeUri: String = "", val clientId: String = "", val clientSecret: String = "", val redirectUri: String = "")

@Path("/api/auth")
class AuthResource @Inject constructor(private val settings: AuthSettings) {

    @Path("start")
    @GET
    fun start(): Response {
        val state = randomString(32)
        val uri = UriBuilder.fromUri(settings.authorizeUri)
                .queryParam("client_id", settings.clientId)
                .queryParam("response_type", "code")
                .queryParam("scope", "accountinformation")
                .queryParam("redirect_uri", settings.redirectUri)
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
        return NewCookie("state", state, "/", null, null, maxAge, secure, true)
    }

    @Path("oauth2callback")
    @GET
    fun oauth2callback() {

    }

    companion object {

        private val symbols: CharArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()

        fun randomString(length: Int): String {
            val random = ThreadLocalRandom.current()
            val buf = CharArray(length)
            for (idx in 0 until buf.size)
                buf[idx] = symbols[random.nextInt(symbols.size)]
            return String(buf)
        }
    }
}