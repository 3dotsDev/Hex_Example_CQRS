package domain.ports.infrastructureport;

import domain.model.Event;
import domain.model.OptimisticLockingException;

import java.util.List;
import java.util.UUID;

public interface IEventStore {

    void store(UUID aggregateId, List<Event> newEvents, int baseVersion) throws OptimisticLockingException;

    List<Event> load(UUID aggregateId);
}
