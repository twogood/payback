package se.activout.payback.oauth2.domain

import javax.ws.rs.core.Response

interface OAuth2Client {
    fun get(oAuth2Token: OAuth2Token, path: String): Response
}