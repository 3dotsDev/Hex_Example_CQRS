package infrastructure.projectionadapter.listener;

import com.google.common.eventbus.Subscribe;
import domain.ports.infrastructureport.IAccountsRepository;
import domain.projection.clientaccount.ClientAccountProjection;
import domain.writemodel.account.AccountDepositedEvent;
import domain.writemodel.account.AccountOpenedEvent;
import domain.writemodel.account.AccountWithdrawnEvent;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listenerobjekt stellt die handles bereit fuer das AccountRepository
 */
public class AccountsListener {
    private final IAccountsRepository accountsRepository;

    public AccountsListener(IAccountsRepository accountsRepository) {
        this.accountsRepository = checkNotNull(accountsRepository);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handle(AccountOpenedEvent event) {
        ClientAccountProjection accountProjection = new ClientAccountProjection(
                event.getAggregateId(), event.getClientId(), event.getBalance(), event.getVersion());
        accountsRepository.save(accountProjection);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handle(AccountDepositedEvent event) {
        accountsRepository.updateBalance(event.getAggregateId(), event.getBalance(), event.getVersion());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handle(AccountWithdrawnEvent event) {
        accountsRepository.updateBalance(event.getAggregateId(), event.getBalance(), event.getVersion());
    }
}
