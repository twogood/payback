package se.activout.payback.session.domain

import se.activout.kronslott.auth.session.SessionUserHelper
import se.activout.payback.app.domain.BackendUser
import se.activout.payback.domain.BankCredential
import se.activout.payback.domain.BankName
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest

class SessionService @Inject constructor() {

    fun setAttribute(httpServletRequest: HttpServletRequest, key: String, value: Any) {
        httpServletRequest.getSession(true).setAttribute(key, value)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAttribute(httpServletRequest: HttpServletRequest, key: String): T? {
        return httpServletRequest.getSession(false)?.getAttribute(key) as T?
    }

    fun <T> getAndRemoveAttribute(httpServletRequest: HttpServletRequest, key: String): T? {
        val value: T? = getAttribute(httpServletRequest, key)
        httpServletRequest.getSession(false)?.removeAttribute(key)
        return value
    }

    fun createSession(httpServletRequest: HttpServletRequest, bankName: BankName, bankCredential: BankCredential) {
        val backendUser = SessionUserHelper.getUser(httpServletRequest.session) ?: BackendUser()
        backendUser.setCredential(bankName, bankCredential)
        SessionUserHelper.createSession(httpServletRequest, backendUser)
    }

}