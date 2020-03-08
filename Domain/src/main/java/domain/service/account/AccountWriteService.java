package domain.service.account;

import com.google.common.eventbus.EventBus;
import domain.commands.account.DepositAccountCommand;
import domain.commands.account.OpenAccountCommand;
import domain.commands.account.WithdrawAccountCommand;
import domain.ports.applicationport.AccountNotFoundException;
import domain.ports.applicationport.IAccountWriteService;
import domain.ports.infrastructureport.IEventStore;
import domain.service.Retrier;
import domain.writemodel.Event;
import domain.writemodel.OptimisticLockingException;
import domain.writemodel.account.Account;
import domain.writemodel.account.NonSufficientFundsException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;

public class AccountWriteService implements IAccountWriteService {
    private final IEventStore eventStore;
    private final EventBus eventBus;
    private final Retrier conflictRetrier;

    public AccountWriteService(IEventStore eventStore, EventBus eventBus) {
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

    private Account process(UUID accountId, Consumer<Account> consumer) throws AccountNotFoundException, OptimisticLockingException {
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
