package domain.ports.leftport;

import domain.model.OptimisticLockingException;
import domain.model.account.Account;
import domain.model.account.NonSufficientFundsException;
import domain.service.account.AccountNotFoundException;
import domain.service.account.DepositAccountCommand;
import domain.service.account.OpenAccountCommand;
import domain.service.account.WithdrawAccountCommand;

import java.util.Optional;
import java.util.UUID;

public interface IAccountService {
    Optional<Account> loadAccount(UUID id);

    Account process(OpenAccountCommand command);

    Account process(DepositAccountCommand command) throws AccountNotFoundException, OptimisticLockingException;

    Account process(WithdrawAccountCommand command)
            throws AccountNotFoundException, OptimisticLockingException, NonSufficientFundsException;
}
