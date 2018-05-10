package se.activout.payback.bank.seb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import mu.KotlinLogging
import se.activout.payback.domain.AccountPlugin
import se.activout.payback.domain.BankAccount
import se.activout.payback.domain.BankCredential
import se.activout.payback.domain.CurrencyCode
import se.activout.payback.oauth2.domain.OAuth2Client
import se.activout.payback.oauth2.domain.OAuth2Token
import se.activout.payback.readEntity
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

private const val BIC = "ESSESESS"

private interface AnyAccount {
    val id: String?
    val bban: String?
    val iban: String?
}

@JsonIgnoreProperties(ignoreUnknown = true)
private data class SebPerson(@JsonProperty("personal_identity_number") val personal_identity_number: String?,
                             @JsonProperty("name") val name: String?)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class SebAmount(
        @JsonProperty("amount") val value: String?,
        @JsonProperty("currency_code") val currency_code: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class SebAccountType(
        @JsonProperty("code") val code: String?,
        @JsonProperty("text") val text: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class SebActions(
        @JsonProperty("withdrawal") val withdrawal: Boolean = false,
        @JsonProperty("deposit") val deposit: Boolean = false,
        @JsonProperty("payment") val payment: Boolean = false
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class ActiveAccount(
        @JsonProperty("id") override val id: String?,
        @JsonProperty("bban") override val bban: String?,
        @JsonProperty("iban") override val iban: String?,
        @JsonProperty("primary_account_owner") val primary_account_owner: SebPerson?,
        @JsonProperty("currency_code") val currency_code: String?,
        @JsonProperty("disposable_amount") val disposable_amount: SebAmount?,
        @JsonProperty("balance") val balance: SebAmount?,
        @JsonProperty("approved_credit_amount") val approved_credit_amount: SebAmount?,
        @JsonProperty("reserved_amount") val reserved_amount: SebAmount?,
        @JsonProperty("account_type") val account_type: SebAccountType?,
        @JsonProperty("custom_name") val custom_name: String?,
        @JsonProperty("actions") val actions: SebActions?
) : AnyAccount

@JsonIgnoreProperties(ignoreUnknown = true)
private data class TerminatedAccount(
        @JsonProperty("id") override val id: String?,
        @JsonProperty("bban") override val bban: String?,
        @JsonProperty("iban") override val iban: String?,
        @JsonProperty("termination_date") val termination_date: String?
) : AnyAccount

@JsonIgnoreProperties(ignoreUnknown = true)
private data class Accounts(@JsonProperty("active_accounts") val active: List<ActiveAccount> = listOf(),
                            @JsonProperty("terminated_accounts") val terminated: List<TerminatedAccount> = listOf()
)

data class SebCredential(val oAuth2Token: OAuth2Token) : BankCredential

class SebAccountPlugin @Inject constructor(@SebInternal private val accountApi: OAuth2Client) : AccountPlugin {

    @Override
    override fun getAccounts(bankCredential: BankCredential): List<BankAccount> {
        val response = accountApi.get((bankCredential as SebCredential).oAuth2Token, "accounts")

        val accounts: Accounts = response.readEntity()
        logger.debug { accounts }

        val result: MutableList<BankAccount> = mutableListOf()

        accounts.active.forEach {
            result.add(BankAccount(
                    bank = SEB,
                    active = true,
                    id = checkNotNull(it.id),
                    iban = checkNotNull(it.iban),
                    bic = BIC,
                    bban = checkNotNull(it.bban),
                    currencyCode = CurrencyCode(checkNotNull(it.currency_code))
            ))
        }

        accounts.terminated.forEach {
            result.add(BankAccount(
                    bank = SEB,
                    active = false,
                    id = checkNotNull(it.id),
                    iban = checkNotNull(it.iban),
                    bic = BIC,
                    bban = checkNotNull(it.bban)
            ))
        }

        return result
    }

}