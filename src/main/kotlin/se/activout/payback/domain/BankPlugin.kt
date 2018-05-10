package se.activout.payback.domain

import se.activout.payback.oauth2.domain.OAuth2Token

data class BankName(val value: String)

interface BankCredential

interface BankPrincipal {
    fun getCredential(bankName: BankName): BankCredential?
}

data class BankOAuth2Settings(
        val baseUrl: String,
        val authorizePath: String,
        val tokenPath: String,
        val clientId: String,
        val clientSecret: String,
        val redirectUrl: String,
        val scopes: List<String>,
        val authorizeParams: Map<String, String> = mapOf(),
        val checkState: Boolean = true
)

interface BankOAuth2 {
    val settings: BankOAuth2Settings
    fun createBankCredential(token: OAuth2Token): BankCredential
}

data class BankOAuth2Credential(val token: OAuth2Token) : BankCredential

interface BankLogin {
    fun oAuth2(bankOAuth2: BankOAuth2)
}


interface BankPlugin {
    val name: BankName
    val accountBackend: AccountPlugin

    fun login(bankLogin: BankLogin)
}