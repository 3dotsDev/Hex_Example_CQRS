package domain.service.account;

import com.google.common.eventbus.EventBus;
import domain.ports.leftport.IAccountService;
import domain.model.Event;
import domain.model.IEventStore;
import domain.model.OptimisticLockingException;
import domain.model.account.Account;
import domain.model.account.NonSufficientFundsException;
import domain.service.Retrier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;

public class AccountService implements IAccountService {
    private final IEventStore eventStore;
    private final EventBus eventBus;
    private final Retrier conflictRetrier;

    public AccountService(IEventStore eventStore, EventBus eventBus) {
        this.eventStore = checkNotNull(eventStore);
        this.eventBus = checkNotNull(eventBus);
        int maxAttempts = 3;
        this.conflictRetrier = new Retrier(singletonList(OptimisticLockingException.class), maxAttempts);
    }

    @Override
    public Optional<Account> loadAccount(UUID id) {
        List<Event> eventStream = eventStore.load(id);
        if (eventStream.isEmpty()) return Optional.empty();
        return Optional.of(new Account(id, eventStream));
    }

    @Override
    public Account process(OpenAccountCommand command) {
        Account account = new Account(randomUUID(), command.getClientId());
        storeAndPublishEvents(account);
        return account;
    }

    @Override
    public Account process(DepositAccountCommand command) throws AccountNotFoundException, OptimisticLockingException {
        return process(command.getId(), (account) -> account.deposit(command.getAmount()));
    }

    @Override
    public Account process(WithdrawAccountCommand command)
            throws AccountNotFoundException, OptimisticLockingException, NonSufficientFundsException {
        return process(command.getId(), (account) -> account.withdraw(command.getAmount()));
    }

    private Account process(UUID accountId, Consumer<Account> consumer)
            throws AccountNotFoundException, OptimisticLockingException {

        return conflictRetrier.get(() -> {
            Optional<Account> possibleAccount = loadAccount(accountId);
            Account account = possibleAccount.orElseThrow(() -> new AccountNotFoundException(accountId));
            consumer.accept(account);
            storeAndPublishEvents(account);
            return account;
        });
    }

    private void storeAndPublishEvents(Account account) throws OptimisticLockingException {
        eventStore.store(account.getId(), account.getNewEvents(), account.getBaseVersion());
        account.getNewEvents().forEach(eventBus::post);
    }
}
