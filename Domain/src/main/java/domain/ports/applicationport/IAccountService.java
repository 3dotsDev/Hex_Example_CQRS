package domain.ports.applicationport;

import domain.commands.account.DepositAccountCommand;
import domain.commands.account.OpenAccountCommand;
import domain.commands.account.WithdrawAccountCommand;
import domain.model.OptimisticLockingException;
import domain.model.account.Account;
import domain.model.account.NonSufficientFundsException;

import java.util.Optional;
import java.util.UUID;

public interface IAccountService {
    Optional<Account> loadAccount(UUID id);

    Account process(OpenAccountCommand command);

    Account process(DepositAccountCommand command) throws AccountNotFoundException, OptimisticLockingException;

    Account process(WithdrawAccountCommand command)
            throws AccountNotFoundException, OptimisticLockingException, NonSufficientFundsException;
}
