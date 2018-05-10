package se.activout.payback.bank.seb

import se.activout.payback.domain.*
import se.activout.payback.oauth2.domain.OAuth2Token
import javax.inject.Inject

val SEB = BankName("seb")

data class SebSettings(val accountApiUrl: String, val oauth2: BankOAuth2Settings)

class Seb @Inject constructor(sebAccountPlugin: SebAccountPlugin, private val sebSettings: SebSettings) : BankPlugin {
    override val name: BankName = SEB

    override val accountBackend: AccountPlugin = sebAccountPlugin

    override fun login(bankLogin: BankLogin) {
        bankLogin.oAuth2(object : BankOAuth2 {
            override val settings = sebSettings.oauth2
            override fun createBankCredential(token: OAuth2Token): BankCredential = BankOAuth2Credential(token)
        })
    }

}