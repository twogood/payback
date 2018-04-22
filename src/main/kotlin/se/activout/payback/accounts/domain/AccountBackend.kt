package se.activout.payback.accounts.domain

import se.activout.payback.oauth2.domain.OAuth2Token

interface AccountBackend {
    fun getAccounts(oAuth2Token: OAuth2Token): Accounts
}