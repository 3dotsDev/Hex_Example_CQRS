package application.commandadapter;

import domain.writemodel.OptimisticLockingException;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
class OptimisticLockingExceptionMapperTest {
    static final ResourceExtension RESOURCES = ResourceExtension.builder()
            .addProvider(OptimisticLockingExceptionMapper.class)
            .addResource(new ConcurrentlyModifiedResource())
            .build();

    @Test
    void returnConflict() {
        Response response = RESOURCES.client()
                .target("/concurrently-modified-resource")
                .request().put(json("{}"));
        response.close();
        assertThat(response.getStatus(), equalTo(409));
    }

    @Consumes(APPLICATION_JSON)
    @Path("/concurrently-modified-resource")
    public static class ConcurrentlyModifiedResource {
        @PUT
        public Response put(String entity) throws OptimisticLockingException {
            throw new OptimisticLockingException("Testing exception mapper");
        }
    }
}