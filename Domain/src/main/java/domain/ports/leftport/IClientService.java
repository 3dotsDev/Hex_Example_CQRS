package domain.ports.leftport;

import domain.model.client.Client;
import domain.service.client.EnrollClientCommand;
import domain.service.client.UpdateClientCommand;

import java.util.Optional;
import java.util.UUID;

public interface IClientService {
    Client process(EnrollClientCommand command);

    Optional<Client> loadClient(UUID id);

    void process(UpdateClientCommand command);
}
