package domain.service.account;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import domain.commands.account.DepositAccountCommand;
import domain.commands.account.OpenAccountCommand;
import domain.commands.account.WithdrawAccountCommand;
import domain.model.Event;
import domain.model.OptimisticLockingException;
import domain.model.account.Account;
import domain.model.account.AccountDepositedEvent;
import domain.model.account.AccountOpenedEvent;
import domain.model.account.AccountWithdrawnEvent;
import domain.ports.infrastructureport.IEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {
    private IEventStore eventStore;
    private EventBusCounter eventBusCounter;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        eventStore = mock(IEventStore.class);
        EventBus eventBus = new EventBus();
        eventBusCounter = new EventBusCounter();
        eventBus.register(eventBusCounter);
        accountService = new AccountService(eventStore, eventBus);
    }

    @Test
    void retryOnAccountWithdrawalConflictsUpToThreeAttempts() {
        Account account = accountService.process(new OpenAccountCommand(randomUUID()));
        UUID id = account.getId();
        accountService.process(new DepositAccountCommand(id, TEN));
        doThrow(OptimisticLockingException.class)
                .doThrow(OptimisticLockingException.class)
                .doCallRealMethod()
                .when(eventStore).store(eq(id), anyListOf(Event.class), anyInt());

        accountService.process(new WithdrawAccountCommand(id, ONE));
        int creationAttempts = 1, depositAttempts = 1, withdrawalAttempts = 3;
        int loadTimes = depositAttempts + withdrawalAttempts;
        int storeTimes = creationAttempts + depositAttempts + withdrawalAttempts;
        verify(eventStore, times(loadTimes)).load(eq(id));
        verify(eventStore, times(storeTimes)).store(eq(id), anyListOf(Event.class), anyInt());
        assertThat(eventBusCounter.eventsCount.get(AccountOpenedEvent.class), equalTo(1));
        assertThat(eventBusCounter.eventsCount.get(AccountDepositedEvent.class), equalTo(1));
        assertThat(eventBusCounter.eventsCount.get(AccountWithdrawnEvent.class), equalTo(1));
        assertThat(eventBusCounter.eventsCount.size(), equalTo(3));
    }

    private static class EventBusCounter {
        Map<Class<?>, Integer> eventsCount = new ConcurrentHashMap<>();

        @Subscribe
        @SuppressWarnings("unused")
        public void handle(Object event) {
            eventsCount.merge(event.getClass(), 1, (oldValue, value) -> oldValue + value);
        }
    }
}