package domain.ports.applicationport;

import domain.commands.client.EnrollClientCommand;
import domain.commands.client.UpdateClientCommand;
import domain.writemodel.client.Client;

import java.util.Optional;
import java.util.UUID;

public interface IClientWriteService {
    Client process(EnrollClientCommand command);

    Optional<Client> loadClient(UUID id);

    void process(UpdateClientCommand command);
}
