package infrastructure.projectionadapter.listener;

import com.google.common.eventbus.Subscribe;
import domain.model.account.AccountDepositedEvent;
import domain.model.account.AccountWithdrawnEvent;
import domain.ports.infrastructureport.ITransactionsRepository;
import domain.projection.accountstransaction.AccountTransactionProjection;

import static com.google.common.base.Preconditions.checkNotNull;
import static domain.projection.accountstransaction.TransactionType.DEPOSIT;
import static domain.projection.accountstransaction.TransactionType.WITHDRAWAL;

public class TransactionsListener {
    private ITransactionsRepository transactionsRepository;

    public TransactionsListener(ITransactionsRepository transactionsRepository) {
        this.transactionsRepository = checkNotNull(transactionsRepository);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handle(AccountDepositedEvent event) {
        AccountTransactionProjection tx = new AccountTransactionProjection(
                event.getAggregateId(), DEPOSIT, event.getAmount(), event.getTimestamp(), event.getVersion());
        transactionsRepository.save(tx);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handle(AccountWithdrawnEvent event) {
        AccountTransactionProjection tx = new AccountTransactionProjection(
                event.getAggregateId(), WITHDRAWAL, event.getAmount(), event.getTimestamp(), event.getVersion());
        transactionsRepository.save(tx);
    }
}
