package application.commandadapter.account;

import domain.model.account.Account;
import domain.ports.applicationport.IAccountService;
import io.dropwizard.jersey.params.UUIDParam;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * RestServiceResource fuer acountservice  NUR get (account)
 */
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/accounts/{id}")
public class AccountResource {

    private final IAccountService accountService;

    public AccountResource(IAccountService accountService) {
        this.accountService = checkNotNull(accountService);
    }

    @GET
    public Response get(@PathParam("id") UUIDParam accountId) {
        Optional<Account> possibleAccount = accountService.loadAccount(accountId.get());
        if (!possibleAccount.isPresent()) return Response.status(NOT_FOUND).build();
        AccountDto accountDto = toDto(possibleAccount.get());
        return Response.ok(accountDto).build();
    }

    /**
     * Mapper
     * @param account
     * @return DTO Object
     */
    private AccountDto toDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setBalance(account.getBalance());
        dto.setClientId(account.getClientId());
        return dto;
    }
}
