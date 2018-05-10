package se.activout.payback.bank.swedbank

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import mu.KotlinLogging
import se.activout.payback.domain.*
import se.activout.payback.oauth2.domain.OAuth2Client
import se.activout.payback.readEntity
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

private const val JSON = """
{
    "account_list": [
        {
            "id": "AsdF01234EfgH4567",
            "currency": "SEK",
            "product": "privatkonto",
            "account_type": "CACC",
            "iban": "SE4880000123459876543219",
            "bic": "SWEDSESS",
            "bban": "1234-5,987 654 321-9",
            "clearingnumber": "1234-5",
            "account_number": "987 654 321-9",
            "balances": [
                {
                    "booked": {
                        "amount": {
                            "currency": "SEK",
                            "content": 100
                        },
                        "date": "2018-01-23"
                    }
                }
            ]
        },
        {
            "id": "AbcD1234eFgH568",
            "currency": "SEK",
            "product": "ungdomskonto",
            "account_type": "CACC",
            "iban": "SE4880000123451234567890",
            "bic": "SWEDSESS",
            "bban": "1234-5,123 456 789-0",
            "clearingnumber": "1234-5",
            "account_number": "123 456 789-0",
            "balances": [
                {
                    "booked": {
                        "amount": {
                            "currency": "SEK",
                            "content": 35000
                        },
                        "date": "2018-01-23"
                    }
                }
            ]
        },
        {
            "id": "Baas786DD5886RT",
            "currency": "SEK",
            "product": "privatkonto",
            "account_type": "CACC",
            "iban": "SE4880000123450002227321",
            "bic": "SWEDSESS",
            "bban": "1234-5,987 654 322-9",
            "clearingnumber": "1234-5",
            "account_number": "987 654 322-9",
            "balances": [
                {
                    "booked": {
                        "amount": {
                            "currency": "SEK",
                            "content": 580
                        },
                        "date": "2018-01-23"
                    }
                }
            ]
        },
        {
            "id": "458A889B8889T784W",
            "currency": "SEK",
            "product": "privatkonto",
            "account_type": "CACC",
            "iban": "SE4880000123455551117123",
            "bic": "SWEDSESS",
            "bban": "1234-5,987 654 323-9",
            "clearingnumber": "1234-5",
            "account_number": "987 654 323-9"
        }
    ]
}
"""

@JsonIgnoreProperties(ignoreUnknown = true)
private data class SwedbankAccount(
        val id: String? = null,
        val currency: String? = null,
        val product: String? = null,
        val account_type: String? = null,
        val iban: String? = null,
        val bic: String? = null,
        val bban: String? = null,
        val clearingnumber: String? = null,
        val account_number: String? = null
)

private data class Wrapper(val account_list: List<SwedbankAccount>? = arrayListOf())

class SwedbankAccountBackend @Inject constructor(@SwedbankInternal private val oAuth2Client: OAuth2Client) : AccountPlugin {
    override fun getAccounts(bankCredential: BankCredential): List<BankAccount> {
        try {

            val response = oAuth2Client.get((bankCredential as BankOAuth2Credential).token, "")
            if (response.status != 200) {
                throw RuntimeException(response.readEntity(String::class.java))
            }

            val wrapper: Wrapper = response.readEntity()
            return checkNotNull(wrapper.account_list).map {
                BankAccount(
                        bank = SWEDBANK,
                        active = true,
                        id = checkNotNull(it.id),
                        iban = checkNotNull(it.iban),
                        bban = checkNotNull(it.bban),
                        bic = checkNotNull(it.bic),
                        currencyCode = CurrencyCode(checkNotNull(it.currency))
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "Swedbank error" }
            throw RuntimeException("Swedbank error", e)
        }
    }
}