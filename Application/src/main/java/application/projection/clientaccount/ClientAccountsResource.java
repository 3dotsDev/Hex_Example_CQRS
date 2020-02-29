package application.projection.clientaccount;

import domain.projection.clientaccount.ClientAccountProjection;
import domain.ports.rightport.IAccountsRepository;
import io.dropwizard.jersey.params.UUIDParam;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("clients/{id}/accounts")
public class ClientAccountsResource {
    private final IAccountsRepository accountsRepository;

    public ClientAccountsResource(IAccountsRepository accountsRepository) {
        this.accountsRepository = checkNotNull(accountsRepository);
    }

    @GET
    public Response get(@PathParam("id") UUIDParam clientId) {
        List<ClientAccountProjection> accounts = accountsRepository.getAccounts(clientId.get());
        return Response.ok(accounts).build();
    }
}
