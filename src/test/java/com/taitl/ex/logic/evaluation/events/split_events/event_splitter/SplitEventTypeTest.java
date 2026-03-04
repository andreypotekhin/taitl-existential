package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SplitEventTypeTest
{
    SplitEventType splitter;

    @BeforeEach
    void setUp()
    {
        splitter = new SplitEventType();
    }

    private static <T> boolean hasEvent(Set<Event<T>> events, Class<?> eventClass)
    {
        return events.stream().anyMatch(eventClass::isInstance);
    }

    @Test
    @DisplayName("Call splits CUD into compound and elementary families")
    void splitCudIntoFamilies()
    {
        String entity = new String("v");
        CUD<String> event = new CUD<>(entity);
        Set<Event<String>> events = new LinkedHashSet<>();
        events.add(event);

        splitter.call(event, events);

        assertEquals(7, events.size());
        assertTrue(hasEvent(events, CUD.class));
        assertTrue(hasEvent(events, CU.class));
        assertTrue(hasEvent(events, Create.class));
        assertTrue(hasEvent(events, Update.class));
        assertTrue(hasEvent(events, Delete.class));
        assertTrue(hasEvent(events, Transit.class));
        assertTrue(hasEvent(events, Port.class));
    }

    @Test
    @DisplayName("Call splits transit into update")
    void splitTransitIntoUpdate()
    {
        Transit<String> event = new Transit<>("old", "new");
        Set<Event<String>> events = new LinkedHashSet<>();
        events.add(event);

        splitter.call(event, events);

        assertEquals(2, events.size());
        assertTrue(hasEvent(events, Transit.class));
        assertTrue(hasEvent(events, Update.class));
    }

    @Test
    @DisplayName("Call splits create into combined and port by default")
    void splitCreateIntoCombinedByDefault()
    {
        String entity = new String("created");
        Create<String> event = new Create<>(entity);
        Set<Event<String>> events = new LinkedHashSet<>();
        events.add(event);

        splitter.call(event, events);

        assertEquals(4, events.size());
        assertTrue(hasEvent(events, Create.class));
        assertTrue(hasEvent(events, CUD.class));
        assertTrue(hasEvent(events, CU.class));

        Port<String> port = events.stream()
                .filter(Port.class::isInstance)
                .map(e -> (Port<String>) e)
                .findFirst()
                .orElseThrow();
        assertSame(entity, port.t0);
        assertSame(entity, port.t1);
    }

    @Test
    @DisplayName("Call skips create to compound split when disabled")
    void skipCreateToCompoundWhenDisabled()
    {
        String entity = new String("created");
        Create<String> event = new Create<>(entity);
        Set<Event<String>> events = new LinkedHashSet<>();
        events.add(event);

        splitter.call(event, events, false);

        assertEquals(1, events.size());
        assertTrue(hasEvent(events, Create.class));
        assertFalse(hasEvent(events, CUD.class));
        assertFalse(hasEvent(events, CU.class));
        assertFalse(hasEvent(events, Port.class));
    }

    @Test
    @DisplayName("Call splits port update branch with update and combined families")
    void splitPortUpdateBranch()
    {
        String before = new String("before");
        String after = new String("after");
        Port<String> event = new Port<>(before, after);
        Set<Event<String>> events = new LinkedHashSet<>();
        events.add(event);

        splitter.call(event, events);

        assertTrue(hasEvent(events, Port.class));
        assertTrue(hasEvent(events, Transit.class));
        assertTrue(hasEvent(events, EntityEvent.class));
        assertTrue(hasEvent(events, Update.class));
        assertTrue(hasEvent(events, CU.class));
        assertTrue(hasEvent(events, UD.class));
        assertTrue(hasEvent(events, CUD.class));
        assertFalse(hasEvent(events, Create.class));
        assertFalse(hasEvent(events, Delete.class));
    }

    @Test
    @DisplayName("Split transit rejects null events with events message")
    void splitTransitRejectsNullEventsWithEventsMessage()
    {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> splitter.splitPort(new Port<>("old", "new"), null));
        assertEquals("Argument 'events' must not be null", error.getMessage());
    }

    @Test
    @DisplayName("Split read and lock rejects null events with events message")
    void splitReadAndLockRejectsNullEventsWithEventsMessage()
    {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> splitter.splitReadAndLock(new ReadAndLock<>("value"), null));
        assertEquals("Argument 'events' must not be null", error.getMessage());
    }
}
