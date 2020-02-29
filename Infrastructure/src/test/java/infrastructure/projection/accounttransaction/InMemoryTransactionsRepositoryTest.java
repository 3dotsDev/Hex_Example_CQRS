package infrastructure.projection.accounttransaction;

import domain.ports.rightport.ITransactionsRepository;
import domain.projection.accountstransaction.AccountTransactionProjection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static domain.projection.accountstransaction.TransactionType.DEPOSIT;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.joda.time.DateTime.now;
import static org.joda.time.DateTimeZone.UTC;

class InMemoryTransactionsRepositoryTest {
    private ITransactionsRepository transactionsRepository =
            new InMemoryTransactionsRepository();

    @Test
    void listEventsSortedByVersion() {
        UUID accountId = randomUUID();
        AccountTransactionProjection tx2 = new AccountTransactionProjection(accountId, DEPOSIT, TEN, now(UTC), 2);
        AccountTransactionProjection tx1 = new AccountTransactionProjection(accountId, DEPOSIT, ONE, now(UTC), 1);
        transactionsRepository.save(tx2);
        transactionsRepository.save(tx1);

        List<AccountTransactionProjection> transactions = transactionsRepository.listByAccount(accountId);
        assertThat(transactions.get(0), equalTo(tx1));
        assertThat(transactions.get(1), equalTo(tx2));
    }
}