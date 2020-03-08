package application.commandadapter.client;

import domain.commands.client.EnrollClientCommand;
import domain.ports.applicationport.IClientWriteService;
import domain.writemodel.client.Client;

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

/**
 * RestServiceResource fuer acountservice  NUR post erstellung (client enrollClientCommand)
 */
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/clients")
public class ClientsWriteResource {
    private IClientWriteService clientService;

    public ClientsWriteResource(IClientWriteService clientService) {
        this.clientService = checkNotNull(clientService);
    }

    @POST
    public Response post(@Valid ClientDto newClientDto) {
        EnrollClientCommand enrollClientCommand = new EnrollClientCommand(
                newClientDto.getName(), new domain.writemodel.client.Email(newClientDto.getEmail()));
        Client client = clientService.process(enrollClientCommand);
        URI clientUri = fromResource(ClientWriteResource.class).build(client.getId());
        return Response.created(clientUri).build();
    }
}
