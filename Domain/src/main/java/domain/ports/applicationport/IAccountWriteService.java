package domain.ports.applicationport;

import domain.commands.account.DepositAccountCommand;
import domain.commands.account.OpenAccountCommand;
import domain.commands.account.WithdrawAccountCommand;
import domain.writemodel.OptimisticLockingException;
import domain.writemodel.account.Account;
import domain.writemodel.account.NonSufficientFundsException;

import java.util.Optional;
import java.util.UUID;

public interface IAccountWriteService {
    Optional<Account> loadAccount(UUID id);

    Account process(OpenAccountCommand command);

    Account process(DepositAccountCommand command) throws AccountNotFoundException, OptimisticLockingException;

    Account process(WithdrawAccountCommand command)
            throws AccountNotFoundException, OptimisticLockingException, NonSufficientFundsException;
}
