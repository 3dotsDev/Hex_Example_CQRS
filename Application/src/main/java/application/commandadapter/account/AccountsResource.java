package application.commandadapter.account;

import domain.model.account.Account;
import domain.ports.applicationport.IAccountService;
import domain.service.account.OpenAccountCommand;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.UriBuilder.fromResource;

@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/accounts")
public class AccountsResource {

    private final IAccountService accountService;

    public AccountsResource(IAccountService accountService) {
        this.accountService = checkNotNull(accountService);
    }

    @POST
    public Response post(@Valid AccountDto accountDto) {
        OpenAccountCommand command = new OpenAccountCommand(accountDto.getClientId());
        Account account = accountService.process(command);
        URI accountUri = fromResource(AccountResource.class).build(account.getId());
        return Response.created(accountUri).build();
    }
}
