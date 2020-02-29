package domain.projection.accountstransaction;

import java.util.List;
import java.util.UUID;

public interface ITransactionsRepository {
    void save(AccountTransactionProjection transactionProjection);

    List<AccountTransactionProjection> listByAccount(UUID accountId);
}
