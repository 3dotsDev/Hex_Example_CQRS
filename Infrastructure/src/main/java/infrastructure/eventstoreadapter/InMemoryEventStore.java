package infrastructure.eventstoreadapter;

import com.google.common.collect.ImmutableList;
import domain.ports.infrastructureport.IEventStore;
import domain.writemodel.Event;
import domain.writemodel.OptimisticLockingException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class InMemoryEventStore implements IEventStore {
    private final Map<UUID, List<Event>> eventStore = new ConcurrentHashMap<>();

    @Override
    public void store(UUID aggregateId, List<Event> newEvents, int baseVersion) throws OptimisticLockingException {
        eventStore.merge(aggregateId, newEvents, (oldValue, value) -> {
            if (baseVersion != oldValue.get(oldValue.size() - 1).getVersion())
                throw new OptimisticLockingException("Base version does not match current stored version");

            return Stream.concat(oldValue.stream(), value.stream()).collect(toList());
        });
    }

    @Override
    public List<Event> load(UUID aggregateId) {
        return ImmutableList.copyOf(eventStore.getOrDefault(aggregateId, emptyList()));
    }
}