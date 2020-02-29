package application.adapter.account.deposits;


import domain.model.OptimisticLockingException;
import domain.service.account.AccountNotFoundException;
import domain.service.account.DepositAccountCommand;
import domain.ports.leftport.IAccountService;
import io.dropwizard.jersey.params.UUIDParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/accounts/{id}/deposits")
public class DepositsResource {
    private final IAccountService accountService;

    public DepositsResource(IAccountService accountService) {
        this.accountService = checkNotNull(accountService);
    }

    @POST
    public Response post(@PathParam("id") UUIDParam accountId, @Valid DepositDto depositDto)
            throws AccountNotFoundException, OptimisticLockingException {

        DepositAccountCommand command = new DepositAccountCommand(accountId.get(), depositDto.getAmount());
        accountService.process(command);
        return Response.noContent().build();
    }
}