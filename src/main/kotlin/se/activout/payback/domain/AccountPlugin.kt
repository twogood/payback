package se.activout.payback.domain


data class CurrencyCode(val value: String)

data class BankAccount(
        val bank: BankName,
        val active: Boolean,
        val id: String,
        val bic: String,
        val bban: String,
        val iban: String,
        val currencyCode: CurrencyCode = CurrencyCode("SEK")
)

interface AccountPlugin {
    fun getAccounts(bankCredential: BankCredential): List<BankAccount>
}