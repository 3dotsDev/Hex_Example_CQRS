package infrastructure.projection.clientaccount;

import com.google.common.collect.ImmutableList;
import domain.projection.clientaccount.ClientAccountProjection;
import domain.projection.clientaccount.IAccountsRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.emptyMap;

public class InMemoryAccountsRepository implements IAccountsRepository {
    private final Map<UUID, Map<UUID, ClientAccountProjection>> clientAccounts = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> accountClientIndex = new ConcurrentHashMap<>();

    @Override
    public void save(ClientAccountProjection accountProjection) {
        clientAccounts.merge(
                accountProjection.getClientId(),
                newAccountsMap(accountProjection),
                (oldValue, value) -> {
                    oldValue.putAll(value);
                    return oldValue;
                }
        );
        accountClientIndex.put(accountProjection.getAccountId(), accountProjection.getClientId());
    }

    @Override
    public void updateBalance(UUID accountId, BigDecimal balance, int version) {
        UUID clientId = accountClientIndex.get(accountId);
        clientAccounts.get(clientId).merge(
                accountId,
                new ClientAccountProjection(accountId, clientId, balance, version),
                (oldValue, value) -> value.getVersion() > oldValue.getVersion() ? value : oldValue);
    }

    @Override
    public List<ClientAccountProjection> getAccounts(UUID clientId) {
        Map<UUID, ClientAccountProjection> accounts = clientAccounts.getOrDefault(clientId, emptyMap());
        return ImmutableList.copyOf(accounts.values());
    }

    private Map<UUID, ClientAccountProjection> newAccountsMap(ClientAccountProjection accountProjection) {
        return new HashMap<UUID, ClientAccountProjection>() {
            {
                put(accountProjection.getAccountId(), accountProjection);
            }
        };
    }
}
