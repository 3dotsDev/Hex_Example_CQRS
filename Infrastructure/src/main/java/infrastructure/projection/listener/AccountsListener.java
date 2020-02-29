package infrastructure.projection.listener;

import com.google.common.eventbus.Subscribe;
import domain.model.account.AccountDepositedEvent;
import domain.model.account.AccountOpenedEvent;
import domain.model.account.AccountWithdrawnEvent;
import domain.projection.clientaccount.ClientAccountProjection;
import domain.projection.clientaccount.IAccountsRepository;

import static com.google.common.base.Preconditions.checkNotNull;

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
