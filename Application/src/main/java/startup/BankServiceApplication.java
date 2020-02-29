package startup;

import application.adapter.OptimisticLockingExceptionMapper;
import application.adapter.account.AccountNotFoundExceptionMapper;
import application.adapter.account.AccountResource;
import application.adapter.account.AccountsResource;
import application.adapter.account.deposits.DepositsResource;
import application.adapter.account.withdrawals.WithdrawalsResource;
import application.adapter.client.ClientResource;
import application.adapter.client.ClientsResource;
import application.projection.accounttransaction.AccountTransactionsResource;
import application.projection.clientaccount.ClientAccountsResource;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import domain.model.IEventStore;
import domain.ports.leftport.*;
import domain.ports.rightport.IAccountsRepository;
import domain.ports.rightport.ITransactionsRepository;
import domain.service.account.AccountService;
import domain.service.client.ClientService;
import infrastructure.eventstore.InMemoryEventStore;
import infrastructure.projection.accounttransaction.InMemoryTransactionsRepository;
import infrastructure.projection.clientaccount.InMemoryAccountsRepository;
import infrastructure.projection.listener.AccountsListener;
import infrastructure.projection.listener.TransactionsListener;
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

        // read model
        ITransactionsRepository transactionsRepository = new InMemoryTransactionsRepository();
        eventBus.register(new TransactionsListener(transactionsRepository));
        environment.jersey().register(new AccountTransactionsResource(transactionsRepository));

        IAccountsRepository accountsRepository = new InMemoryAccountsRepository();
        eventBus.register(new AccountsListener(accountsRepository));
        environment.jersey().register(new ClientAccountsResource(accountsRepository));
    }
}