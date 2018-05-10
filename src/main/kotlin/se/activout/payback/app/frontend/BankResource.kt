package se.activout.payback.app.frontend

import se.activout.payback.domain.BankName
import se.activout.payback.domain.BankService
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/api/banks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class BankResource @Inject constructor(private val bankService: BankService) {

    @GET
    fun list(): Iterable<String> {
        return bankService.getBankNames().map(BankName::toString)
    }

}
