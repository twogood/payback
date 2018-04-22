package se.activout.payback.accounts.frontend

import io.dropwizard.auth.Auth
import mu.KotlinLogging
import se.activout.payback.app.domain.BackendUser
import se.activout.payback.accounts.domain.AccountService
import se.activout.payback.accounts.domain.Accounts
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

private val logger = KotlinLogging.logger {}

@Path("/api/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class AccountResource @Inject constructor(private val accountService: AccountService) {

    @GET
    fun list(@Auth user: BackendUser): Accounts {
        val accounts: Accounts = accountService.getAccounts(user.oAuth2Token)

        return accounts
    }


}