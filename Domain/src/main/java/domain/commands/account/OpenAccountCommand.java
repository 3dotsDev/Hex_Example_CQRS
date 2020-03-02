package domain.commands.account;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class OpenAccountCommand {
    private final UUID clientId;

    public OpenAccountCommand(UUID clientId) {
        this.clientId = checkNotNull(clientId);
    }

    public UUID getClientId() {
        return clientId;
    }
}
