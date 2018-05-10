package se.activout.payback.app.frontend

import io.dropwizard.auth.Auth
import mu.KotlinLogging
import org.glassfish.jersey.server.ManagedAsync
import se.activout.payback.app.domain.BackendUser
import se.activout.payback.domain.AccountService
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.MediaType

private val logger = KotlinLogging.logger {}

data class BankAccountView(
        val bank: String,
        val active: Boolean,
        val id: String,
        val bic: String,
        val bban: String,
        val iban: String,
        val currencyCode: String
)

@Path("/api/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class AccountResource @Inject constructor(private val accountService: AccountService) {

    @GET
    @ManagedAsync
    fun list(@Auth user: BackendUser, @Suspended asyncResponse: AsyncResponse) {
        val accounts: List<BankAccountView> = accountService.getAccounts(user)
                .map {
                    it.run {
                        BankAccountView(bank.value, active, id, bic, bban, iban, currencyCode.value)
                    }
                }
        asyncResponse.resume(accounts)
    }


}