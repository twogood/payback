package se.activout.payback.bank.swedbank

import se.activout.payback.domain.*
import se.activout.payback.oauth2.domain.OAuth2Token
import javax.inject.Inject

val SWEDBANK = BankName("swedbank")

data class SwedbankSettings(val accountApiUrl: String, val oauth2: BankOAuth2Settings)

class Swedbank @Inject constructor(swedbankAccountBackend: SwedbankAccountBackend, private val swedbankSettings: SwedbankSettings) : BankPlugin {
    override val name: BankName = SWEDBANK
    override val accountBackend: AccountPlugin = swedbankAccountBackend

    override fun login(bankLogin: BankLogin) {
        bankLogin.oAuth2(object : BankOAuth2 {
            override val settings = swedbankSettings.oauth2
            override fun createBankCredential(token: OAuth2Token): BankCredential = BankOAuth2Credential(token)
        })
    }
}
