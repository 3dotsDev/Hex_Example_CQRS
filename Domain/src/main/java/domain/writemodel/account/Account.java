package domain.writemodel.account;

import domain.writemodel.Aggregate;
import domain.writemodel.Event;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static java.math.BigDecimal.ZERO;
import static org.joda.time.DateTime.now;
import static org.joda.time.DateTimeZone.UTC;

public class Account extends Aggregate {
    private BigDecimal balance;
    private UUID clientId;

    /**
     * event creation  AccountOpenedEvent (matching to command)
     * @param id
     * @param clientId
     */
    public Account(UUID id, UUID clientId) {
        super(id);
        AccountOpenedEvent accountOpenedEvent = new AccountOpenedEvent(
                id, now(UTC), getNextVersion(), clientId, ZERO);
        applyNewEvent(accountOpenedEvent);
    }

    public Account(UUID id, List<Event> eventStream) {
        super(id, eventStream);
    }

    /**
     * event creation AccountDepositedEvent (matching to command)
     * @param amount
     */
    public void deposit(BigDecimal amount) {
        BigDecimal newBalance = balance.add(amount);
        AccountDepositedEvent accountDepositedEvent = new AccountDepositedEvent(
                getId(), now(UTC), getNextVersion(), amount, newBalance);
        applyNewEvent(accountDepositedEvent);
    }

    /**
     * event creation  AccountWithdrawnEvent (matching to command)
     * @param amount
     */
    public void withdraw(BigDecimal amount) {
        BigDecimal newBalance = balance.subtract(amount);
        if (newBalance.signum() == -1) throw new NonSufficientFundsException(getId(), balance, amount);
        AccountWithdrawnEvent accountWithdrawnEvent = new AccountWithdrawnEvent(
                getId(), now(UTC), getNextVersion(), amount, newBalance);
        applyNewEvent(accountWithdrawnEvent);
    }

    @SuppressWarnings("unused")
    private void apply(AccountOpenedEvent event) {
        clientId = event.getClientId();
        balance = event.getBalance();
    }

    @SuppressWarnings("unused")
    private void apply(AccountDepositedEvent event) {
        balance = event.getBalance();
    }

    @SuppressWarnings("unused")
    private void apply(AccountWithdrawnEvent event) { balance = event.getBalance(); }

    public BigDecimal getBalance() {
        return balance;
    }

    public UUID getClientId() {
        return clientId;
    }
}
