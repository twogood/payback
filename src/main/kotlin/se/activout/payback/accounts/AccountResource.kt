package se.activout.payback.accounts

import io.dropwizard.auth.Auth
import mu.KotlinLogging
import org.glassfish.jersey.logging.LoggingFeature
import se.activout.payback.BackendUser
import se.activout.payback.authorizationHeader
import se.activout.payback.div
import se.activout.payback.readEntity
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.MediaType

data class AccountApiSettings(val url: String = "")

private val logger = KotlinLogging.logger {}

@Path("/api/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class AccountResource @Inject constructor(private val settings: AccountApiSettings) {

    @GET
    fun list(@Auth user: BackendUser): Accounts {
        val client = ClientBuilder.newClient()
        client.register(LoggingFeature(Logger.getLogger("CLIENT"), Level.FINE, LoggingFeature.Verbosity.PAYLOAD_ANY, LoggingFeature.DEFAULT_MAX_ENTITY_SIZE));
        val webTarget = client.target(settings.url) / "accounts"

        val response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .authorizationHeader(user)
                .get()
        logger.debug { response }

        val accounts: Accounts = response.readEntity()
        logger.debug { accounts }

        return accounts
    }

}