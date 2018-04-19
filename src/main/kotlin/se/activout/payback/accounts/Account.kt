package se.activout.payback.accounts

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

interface AnyAccount {
    val id: String
    val bban: String
    val iban: String
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Person(@JsonProperty("personal_identity_number") val personal_identity_number: String = "",
                  @JsonProperty("name") val name: String = "")

@JsonIgnoreProperties(ignoreUnknown = true)
data class Amount(
        @JsonProperty("amount") val value: String = "",
        @JsonProperty("currency_code") val currency_code: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AccountType(
        @JsonProperty("code") val code: String = "",
        @JsonProperty("text") val text: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Actions(
        @JsonProperty("withdrawal") val withdrawal: Boolean = false,
        @JsonProperty("deposit") val deposit: Boolean = false,
        @JsonProperty("payment") val payment: Boolean = false
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ActiveAccount(
        @JsonProperty("id") override val id: String = "",
        @JsonProperty("bban") override val bban: String = "",
        @JsonProperty("iban") override val iban: String = "",
        @JsonProperty("primary_account_owner") val primary_account_owner: Person = Person(),
        @JsonProperty("currency_code") val currency_code: String = "",
        @JsonProperty("disposable_amount") val disposable_amount: Amount,
        @JsonProperty("balance") val balance: Amount = Amount(),
        @JsonProperty("approved_credit_amount") val approved_credit_amount: Amount = Amount(),
        @JsonProperty("reserved_amount") val reserved_amount: Amount = Amount(),
        @JsonProperty("account_type") val account_type: AccountType = AccountType(),
        @JsonProperty("custom_name") val custom_name: String = "",
        @JsonProperty("actions") val actions: Actions = Actions()
) : AnyAccount

@JsonIgnoreProperties(ignoreUnknown = true)
data class TerminatedAccount(
        @JsonProperty("termination_date") val termination_date: String = "",
        @JsonProperty("id") override val id: String = "",
        @JsonProperty("bban") override val bban: String = "",
        @JsonProperty("iban") override val iban: String = ""
) : AnyAccount

@JsonIgnoreProperties(ignoreUnknown = true)
data class Accounts(@JsonProperty("active_accounts") val active: List<ActiveAccount> = listOf(),
                    @JsonProperty("terminated_accounts") val terminated: List<TerminatedAccount> = listOf()
)
