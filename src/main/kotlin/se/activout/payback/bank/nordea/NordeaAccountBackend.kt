package se.activout.payback.bank.nordea

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import mu.KotlinLogging
import se.activout.payback.domain.*
import se.activout.payback.readEntity
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

const val JSON = """
{
  "groupHeader": {
    "messageIdentification": "3JOZ-glIHNw",
    "creationDateTime": "2018-04-27T17:49:57.922Z",
    "httpCode": 200
  },
  "response": {
    "accounts": [
      {
        "_id": "FI6593857450293470-EUR",
        "country": "FI",
        "accountNumber": {
          "value": "FI6593857450293470",
          "_type": "IBAN"
        },
        "currency": "EUR",
        "ownerName": "owner",
        "product": "SHEKKITILI",
        "accountType": "Current",
        "availableBalance": "13983.92",
        "bookedBalance": "13983.92",
        "valueDatedBalance": "13983.92",
        "_links": [
          {
            "rel": "details",
            "href": "/v2/accounts/FI6593857450293470-EUR"
          },
          {
            "rel": "transactions",
            "href": "/v2/accounts/FI6593857450293470-EUR/transactions"
          }
        ]
      },
      {
        "_id": "FI7473834510057469-EUR",
        "country": "FI",
        "accountNumber": {
          "value": "FI7473834510057469",
          "_type": "IBAN"
        },
        "currency": "EUR",
        "ownerName": "owner",
        "product": "SHEKKITILI",
        "accountType": "Current",
        "availableBalance": "1123.60",
        "bookedBalance": "1111.50",
        "valueDatedBalance": "1111.50",
        "_links": []
      },
      {
        "_id": "FI1511123500015653-EUR",
        "country": "FI",
        "accountNumber": {
          "value": "FI1511123500015653",
          "_type": "IBAN"
        },
        "currency": "EUR",
        "ownerName": "owner",
        "product": "SHEKKITILI",
        "accountType": "Current",
        "_links": [
          {
            "rel": "details",
            "href": "/v2/accounts/FI1511123500015653-EUR"
          }
        ]
      }
    ]
  }
}
"""

@JsonIgnoreProperties(ignoreUnknown = true)
private data class GroupHeader(val messageIdentification: String? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class AccountNumber(val value: String? = null, val _type: String? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class NordeaAccount(val _id: String? = null,
                                 val country: String? = null,
                                 val currency: String? = null,
                                 val accountNumber: AccountNumber? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class AccountResponse(val accounts: List<NordeaAccount>? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class Wrapper(
        val groupHeader: GroupHeader? = null,
        val response: AccountResponse? = null
)

class NordeaAccountBackend @Inject constructor(private val nordeaOAuth2Client: NordeaOAuth2Client) : AccountPlugin {
    override fun getAccounts(bankCredential: BankCredential): List<BankAccount> {
        try {
//            check(bankCredential is BankOAuth2Credential)

            val response = nordeaOAuth2Client.get((bankCredential as BankOAuth2Credential).token, "")

            if (response.status != 200) {
                throw RuntimeException(response.readEntity(String::class.java))
            }

            val wrapper: Wrapper = response.readEntity()

            /*
            val wrapper: Wrapper = ObjectMapper().registerKotlinModule().readValue(JSON, Wrapper::class.java)
            */
            return checkNotNull(wrapper.response?.accounts).map {
                check(it.accountNumber?._type == "IBAN")
                BankAccount(
                        bank = NORDEA,
                        active = true,
                        currencyCode = CurrencyCode(checkNotNull(it.currency)),
                        id = checkNotNull(it._id),
                        iban = checkNotNull(it.accountNumber?.value),
                        bban = checkNotNull(it.accountNumber?.value),
                        bic = "NDEAFIHH"
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "Nordea error" }
            throw RuntimeException("Nordea error", e)
        }
    }
}