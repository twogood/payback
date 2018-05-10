package se.activout.payback.oauth2.frontend

import org.eclipse.jetty.server.session.Session
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.NewCookie

object SessionHelper {

    fun getSessionCookie(httpServletRequest: HttpServletRequest): NewCookie? {
        val session = httpServletRequest.session as Session
        val cookie = session.sessionHandler.getSessionCookie(session, httpServletRequest.contextPath, httpServletRequest.isSecure)
        return NewCookie(
                cookie.name,
                cookie.value,
                cookie.path,
                cookie.domain,
                cookie.version,
                cookie.comment,
                cookie.maxAge.toInt(),
                null,
                cookie.isSecure,
                cookie.isHttpOnly
        )
    }
}