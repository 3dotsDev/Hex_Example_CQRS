package domain.ports.infrastructureport;

import domain.projection.accountstransaction.AccountTransactionProjection;

import java.util.List;
import java.util.UUID;

public interface ITransactionsRepository {
    void save(AccountTransactionProjection transactionProjection);

    List<AccountTransactionProjection> listByAccount(UUID accountId);
}
