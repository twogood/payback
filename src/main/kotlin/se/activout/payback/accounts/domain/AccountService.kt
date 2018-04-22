package se.activout.payback.accounts.domain

import se.activout.payback.oauth2.domain.OAuth2Token
import javax.inject.Inject

class AccountService @Inject constructor(private val accountBackend: AccountBackend) {
    fun getAccounts(oAuth2Token: OAuth2Token): Accounts {
        return accountBackend.getAccounts(oAuth2Token)
    }
}