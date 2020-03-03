package application.projectionadapter.accounttransaction;

import domain.ports.infrastructureport.ITransactionsRepository;
import domain.projection.accountstransaction.AccountTransactionProjection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
/**
 * RestServiceResource fuer Transactionen eines Accounts
 * Sind Projectionen aus dem Store pro Account -> Bewegungsdaten
 */
@Produces(APPLICATION_JSON)
@Path("/accounts/{id}/transactions")
public class AccountTransactionsResource {
    private ITransactionsRepository transactionsRepository;

    public AccountTransactionsResource(ITransactionsRepository transactionsRepository) {
        this.transactionsRepository = checkNotNull(transactionsRepository);
    }

    /**
     * Projektsionsdaten der Transaktionen eines Accounts
     * Zeigt die Buchungen auf dem Account
     * @param accountId
     * @return Liste von Buchungen auf dem Account
     */
    @GET
    public Response get(@PathParam("id") UUID accountId) {
        List<AccountTransactionProjection> transactionProjections = transactionsRepository.listByAccount(accountId);
        return Response.ok(transactionProjections).build();
    }
}
