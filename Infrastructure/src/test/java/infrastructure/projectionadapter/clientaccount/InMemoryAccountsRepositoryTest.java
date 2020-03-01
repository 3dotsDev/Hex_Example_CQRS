package infrastructure.projectionadapter.clientaccount;

import domain.ports.infrastructureport.IAccountsRepository;
import domain.projection.clientaccount.ClientAccountProjection;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static java.math.BigDecimal.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class InMemoryAccountsRepositoryTest {
    private IAccountsRepository accountsRepository =
            new InMemoryAccountsRepository();

    @Test
    void ignoreEventOutOfOrder() {
        UUID clientId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        accountsRepository.save(new ClientAccountProjection(accountId, clientId, ZERO, 1));
        accountsRepository.updateBalance(accountId, TEN, 3);
        accountsRepository.updateBalance(accountId, ONE, 2);
        assertThat(accountsRepository.getAccounts(clientId).get(0).getBalance(), equalTo(TEN));
    }
}