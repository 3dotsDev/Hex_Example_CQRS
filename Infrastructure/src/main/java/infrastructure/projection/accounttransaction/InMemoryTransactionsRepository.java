package infrastructure.projection.accounttransaction;

import domain.projection.accountstransaction.AccountTransactionProjection;
import domain.ports.rightport.ITransactionsRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class InMemoryTransactionsRepository  implements ITransactionsRepository {
    private Map<UUID, List<AccountTransactionProjection>> accountTransactions = new ConcurrentHashMap<>();

    @Override
    public List<AccountTransactionProjection> listByAccount(UUID accountId) {
        return accountTransactions.getOrDefault(accountId, emptyList()).stream()
                .sorted(comparing(AccountTransactionProjection::getVersion))
                .collect(toList());
    }

    @Override
    public void save(AccountTransactionProjection transactionProjection) {
        accountTransactions.merge(
                transactionProjection.getAccountId(),
                newArrayList(transactionProjection),
                (oldValue, value) -> Stream.concat(oldValue.stream(), value.stream()).collect(toList()));
    }
}
