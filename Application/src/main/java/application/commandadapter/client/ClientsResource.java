package application.commandadapter.client;

import domain.model.client.Client;
import domain.ports.applicationport.IClientService;
import domain.service.client.EnrollClientCommand;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.UriBuilder.fromResource;

@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/clients")
public class ClientsResource {
    private IClientService clientService;

    public ClientsResource(IClientService clientService) {
        this.clientService = checkNotNull(clientService);
    }

    @POST
    public Response post(@Valid ClientDto newClientDto) {
        EnrollClientCommand enrollClientCommand = new EnrollClientCommand(
                newClientDto.getName(), new domain.model.client.Email(newClientDto.getEmail()));
        Client client = clientService.process(enrollClientCommand);
        URI clientUri = fromResource(ClientResource.class).build(client.getId());
        return Response.created(clientUri).build();
    }
}
