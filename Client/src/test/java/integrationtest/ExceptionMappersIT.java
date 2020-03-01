package integrationtest;

import application.commandadapter.OptimisticLockingExceptionMapper;
import application.commandadapter.account.AccountNotFoundExceptionMapper;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExceptionMappersIT extends BaseIT {

    @Test
    void registeredExceptionMappers() {
        Set<Class<?>> classes = BANK_SERVICE.getEnvironment().jersey().getResourceConfig().getClasses();
        assertTrue(classes.contains(OptimisticLockingExceptionMapper.class));
        assertTrue(classes.contains(AccountNotFoundExceptionMapper.class));
    }
}