package se.activout.payback.session.domain

import se.activout.kronslott.auth.session.SessionUserHelper
import se.activout.payback.app.domain.BackendUser
import se.activout.payback.oauth2.domain.OAuth2Token
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest

class SessionService @Inject constructor() {

    fun createSession(httpServletRequest: HttpServletRequest, oAuth2Token: OAuth2Token) {
        SessionUserHelper.createSession(httpServletRequest, BackendUser("user", oAuth2Token))
    }

}