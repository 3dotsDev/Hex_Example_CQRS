package startup;

import application.commandadapter.OptimisticLockingExceptionMapper;
import application.commandadapter.account.AccountNotFoundExceptionMapper;
import application.commandadapter.account.AccountResource;
import application.commandadapter.account.AccountsResource;
import application.commandadapter.account.deposits.DepositsResource;
import application.commandadapter.account.withdrawals.WithdrawalsResource;
import application.commandadapter.client.ClientResource;
import application.commandadapter.client.ClientsResource;
import application.projectionadapter.accounttransaction.AccountTransactionsResource;
import application.projectionadapter.clientaccount.ClientAccountsResource;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import domain.ports.applicationport.IAccountService;
import domain.ports.applicationport.IClientService;
import domain.ports.infrastructureport.IAccountsRepository;
import domain.ports.infrastructureport.IEventStore;
import domain.ports.infrastructureport.ITransactionsRepository;
import domain.service.account.AccountService;
import domain.service.client.ClientService;
import infrastructure.eventstoreadapter.InMemoryEventStore;
import infrastructure.projectionadapter.accounttransaction.InMemoryTransactionsRepository;
import infrastructure.projectionadapter.clientaccount.InMemoryAccountsRepository;
import infrastructure.projectionadapter.listener.AccountsListener;
import infrastructure.projectionadapter.listener.TransactionsListener;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.logging.LoggingFeature;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;
import static org.glassfish.jersey.logging.LoggingFeature.DEFAULT_LOGGER_NAME;
import static org.glassfish.jersey.logging.LoggingFeature.Verbosity.PAYLOAD_ANY;

public class BankServiceApplication extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new BankServiceApplication().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        registerFilters(environment);
        registerExceptionMappers(environment);
        registerHypermediaSupport(environment);
        registerResources(environment);
    }

    private void registerFilters(Environment environment) {
        environment.jersey().register(new LoggingFeature(getLogger(DEFAULT_LOGGER_NAME), INFO, PAYLOAD_ANY, 1024));
    }

    private void registerExceptionMappers(Environment environment) {
        environment.jersey().register(AccountNotFoundExceptionMapper.class);
        environment.jersey().register(OptimisticLockingExceptionMapper.class);
    }

    private void registerHypermediaSupport(Environment environment) {
        environment.jersey().getResourceConfig().register(DeclarativeLinkingFeature.class);
    }

    private void registerResources(Environment environment) {
        IEventStore eventStore = new InMemoryEventStore();
        EventBus eventBus = new AsyncEventBus(newSingleThreadExecutor());

        // domain model
        IAccountService accountService = new AccountService(eventStore, eventBus);
        environment.jersey().register(new AccountsResource(accountService));
        environment.jersey().register(new AccountResource(accountService));
        environment.jersey().register(new DepositsResource(accountService));
        environment.jersey().register(new WithdrawalsResource(accountService));

        IClientService clientService = new ClientService(eventStore);
        environment.jersey().register(new ClientsResource(clientService));
        environment.jersey().register(new ClientResource(clientService));

        // read model (projections)
        ITransactionsRepository transactionsRepository = new InMemoryTransactionsRepository();
        eventBus.register(new TransactionsListener(transactionsRepository));
        environment.jersey().register(new AccountTransactionsResource(transactionsRepository));

        IAccountsRepository accountsRepository = new InMemoryAccountsRepository();
        eventBus.register(new AccountsListener(accountsRepository));
        environment.jersey().register(new ClientAccountsResource(accountsRepository));
    }
}