package application.commandadapter.client;

import domain.commands.client.UpdateClientCommand;
import domain.model.client.Client;
import domain.ports.applicationport.IClientService;
import io.dropwizard.jersey.params.UUIDParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * RestServiceResource fuer acountservice  NUR get und put(client abfrage oder update) -> weil ID in der URL beide hier
 */
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("/clients/{id}")
public class ClientResource {
    private IClientService clientService;

    public ClientResource(IClientService clientService) {
        this.clientService = checkNotNull(clientService);
    }

    /**
     * Neuen Client erzeugen
     * @param clientId
     * @return
     */
    @GET
    public Response get(@PathParam("id") UUIDParam clientId) {
        Optional<Client> possibleClient = clientService.loadClient(clientId.get());
        if (!possibleClient.isPresent()) return Response.status(NOT_FOUND).build();
        ClientDto clientDto = toDto(possibleClient.get());
        return Response.ok(clientDto).build();
    }

    /**
     * Bestegenden Client updaten
     * @param clientId explizite ClientId
     * @param clientDto DTOObject
     * @return
     */
    @PUT
    public Response put(@PathParam("id") UUIDParam clientId, @Valid @NotNull ClientDto clientDto) {
        UpdateClientCommand command = new UpdateClientCommand(
                clientId.get(), clientDto.getName(), new domain.model.client.Email(clientDto.getEmail()));
        clientService.process(command);
        return Response.noContent().build();
    }

    /**
     * Mapper
     * @param client
     * @return DTOObject
     */
    private ClientDto toDto(Client client) {
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setEmail(client.getEmail().getValue());
        return dto;
    }
}
