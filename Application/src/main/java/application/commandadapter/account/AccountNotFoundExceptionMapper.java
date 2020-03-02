package application.commandadapter.account;

import domain.ports.applicationport.AccountNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class AccountNotFoundExceptionMapper implements ExceptionMapper<AccountNotFoundException>  {
    @Override
    public Response toResponse(AccountNotFoundException exception) {
        return Response.status(NOT_FOUND).build();
    }
}
