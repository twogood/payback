package se.activout.payback.accounts.backend

import mu.KotlinLogging
import se.activout.payback.accounts.domain.AccountBackend
import se.activout.payback.accounts.domain.Accounts
import se.activout.payback.oauth2.domain.OAuth2Client
import se.activout.payback.oauth2.domain.OAuth2Token
import se.activout.payback.readEntity
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

class AccountBackendImpl @Inject constructor(@AccountApi private val accountApi: OAuth2Client) : AccountBackend {

    @Override
    override fun getAccounts(oAuth2Token: OAuth2Token): Accounts {
        val response = accountApi.get(oAuth2Token, "accounts")

        val accounts: Accounts = response.readEntity()
        logger.debug { accounts }
        return accounts
    }

}