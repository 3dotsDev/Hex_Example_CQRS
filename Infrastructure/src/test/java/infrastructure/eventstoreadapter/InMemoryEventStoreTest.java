package infrastructure.eventstoreadapter;

import domain.ports.infrastructureport.IEventStore;
import domain.writemodel.Event;
import domain.writemodel.OptimisticLockingException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.joda.time.DateTime.now;
import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class InMemoryEventStoreTest {
    private IEventStore eventStore = new InMemoryEventStore();

    @Test
    void storeEventsInOrder() {
        UUID aggregateId = randomUUID();
        Event e1 = new Event(aggregateId, now(UTC), 1){};
        Event e2 = new Event(aggregateId, now(UTC), 2){};
        Event e3 = new Event(aggregateId, now(UTC), 3){};
        eventStore.store(aggregateId, newArrayList(e1), 0);
        eventStore.store(aggregateId, newArrayList(e2), 1);
        eventStore.store(aggregateId, newArrayList(e3), 2);

        List<Event> eventStream = eventStore.load(aggregateId);
        assertThat(eventStream.size(), equalTo(3));
        assertThat(eventStream.get(0), equalTo(e1));
        assertThat(eventStream.get(1), equalTo(e2));
        assertThat(eventStream.get(2), equalTo(e3));
    }

    @Test
    void optimisticLocking() {
        UUID aggregateId = randomUUID();
        Event e1 = new Event(aggregateId, now(UTC), 1){};
        Event e2 = new Event(aggregateId, now(UTC), 2){};
        Event e3 = new Event(aggregateId, now(UTC), 2){};
        eventStore.store(aggregateId, newArrayList(e1), 0);
        eventStore.store(aggregateId, newArrayList(e2), 1);
        assertThrows(
                OptimisticLockingException.class,
                () -> eventStore.store(aggregateId, newArrayList(e3), 1)
        );
    }

    @Test
    void loadedEventStreamIsImmutable() {
        UUID aggregateId = randomUUID();
        Event e1 = new Event(aggregateId, now(UTC), 1){};
        eventStore.store(aggregateId, newArrayList(e1), 0);
        assertThrows(
                UnsupportedOperationException.class,
                () -> eventStore.load(aggregateId).add(mock(Event.class))
        );
    }
}