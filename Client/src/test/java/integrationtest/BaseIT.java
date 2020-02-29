package integrationtest;


import integrationtest.client.ResourcesClient;
import integrationtest.client.ResourcesDtos;
import integrationtest.setup.StateSetup;
import io.dropwizard.Configuration;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import startup.BankServiceApplication;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;

@ExtendWith(DropwizardExtensionsSupport.class)
abstract class BaseIT {
    protected static final DropwizardAppExtension<Configuration> BANK_SERVICE =
            new DropwizardAppExtension<>(BankServiceApplication.class, resourceFilePath("integration.yml"));

    protected static ResourcesClient resourcesClient;
    protected static ResourcesDtos resourcesDtos;
    protected static StateSetup stateSetup;

    @BeforeAll
    public static void setUpBaseClass() {
        resourcesClient = new ResourcesClient(BANK_SERVICE.getEnvironment(), BANK_SERVICE.getLocalPort());
        resourcesDtos = new ResourcesDtos(BANK_SERVICE.getObjectMapper());
        stateSetup = new StateSetup(resourcesClient, resourcesDtos);
    }
}
