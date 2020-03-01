package domain.ports.infrastructureport;

import domain.projection.clientaccount.ClientAccountProjection;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IAccountsRepository {
    void save(ClientAccountProjection accountProjection);

    void updateBalance(UUID accountId, BigDecimal balance, int version);

    List<ClientAccountProjection> getAccounts(UUID clientId);
}
