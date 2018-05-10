package se.activout.payback.bank.nordea

import se.activout.payback.domain.*
import se.activout.payback.oauth2.domain.OAuth2Token
import javax.inject.Inject

val NORDEA = BankName("nordea")

data class NordeaSettings(val accountApiUrl: String, val oauth2: BankOAuth2Settings)

class Nordea @Inject constructor(nordeaAccountBackend: NordeaAccountBackend, private val nordeaSettings: NordeaSettings) : BankPlugin {
    override val name: BankName = NORDEA
    override val accountBackend: AccountPlugin = nordeaAccountBackend

    override fun login(bankLogin: BankLogin) {
        bankLogin.oAuth2(object : BankOAuth2 {
            override val settings = nordeaSettings.oauth2
            override fun createBankCredential(token: OAuth2Token): BankCredential = BankOAuth2Credential(token)
        })
    }
}