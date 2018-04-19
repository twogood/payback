package se.activout.payback

import se.activout.payback.oauth2.OAuth2Token
import java.security.Principal
import javax.ws.rs.client.Invocation

fun Invocation.Builder.authorizationHeader(user: BackendUser): Invocation.Builder {
    return header("Authorization", user.authorizationHeaderValue)
}

class BackendUser(private val name: String, private val oAuth2Token: OAuth2Token) : Principal {
    override fun getName(): String {
        return name
    }

    val authorizationHeaderValue: String
        get() = "Bearer ${oAuth2Token.accessToken}"
}