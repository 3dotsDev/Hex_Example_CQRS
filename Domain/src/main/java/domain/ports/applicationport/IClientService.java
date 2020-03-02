package domain.ports.applicationport;

import domain.commands.client.EnrollClientCommand;
import domain.commands.client.UpdateClientCommand;
import domain.model.client.Client;

import java.util.Optional;
import java.util.UUID;

public interface IClientService {
    Client process(EnrollClientCommand command);

    Optional<Client> loadClient(UUID id);

    void process(UpdateClientCommand command);
}
