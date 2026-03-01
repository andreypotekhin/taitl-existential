package com.taitl.ex.logic.evaluation.split_events;

import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EventSplitterTest
{
    private static final class TestEventSplitter extends EventSplitter
    {
        private <T> Set<Event<T>> splitTransitPublic(Transit<T> transit, Set<Event<T>> set)
        {
            return splitTransit(transit, set);
        }
    }

    private static <T> boolean hasEvent(Set<Event<T>> events, Class<?> eventClass)
    {
        return events.stream().anyMatch(eventClass::isInstance);
    }

    @Test
    void splitTransitUsesUpdatedEntityForChange()
    {
        EventSplitter splitter = new EventSplitter();
        String oldValue = new String("old");
        String newValue = new String("new");

        Set<Event<String>> events = splitter.splitEvent(new Transit<>(oldValue, newValue));

        Change<String> change = events.stream()
                .filter(event -> event instanceof Change<?>)
                .map(event -> (Change<String>) event)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Change event not found"));

        assertSame(newValue, change.t);
    }

    @Test
    void splitRuntimeKeySplitsByUnderlyingEventAndKeepsTypeKey()
    {
        EventSplitter splitter = new EventSplitter();
        String oldValue = new String("old");
        String newValue = new String("new");
        TypeKey<String> typeKey = new TypeKey<>(String.class);
        RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(new Transit<>(oldValue, newValue), typeKey, newValue, false);

        Set<RuntimeKey<String>> splitKeys = splitter.split(runtimeKey);

        assertTrue(splitKeys.stream().anyMatch(key -> key.toString().startsWith("Mutate<String>+")));
        assertTrue(splitKeys.stream().allMatch(key -> key.typeKey().toString().equals("String")));
    }

    @Test
    void splitRuntimeKeyAlsoSplitsTypeKeyByGenericsDimension()
    {
        EventSplitter splitter = new EventSplitter();
        RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(
                new Read<>("value"),
                TypeKey.valueOf("T<A<X>,B<Y>>"),
                "value",
                false);

        Set<RuntimeKey<String>> splitKeys = splitter.split(runtimeKey);
        Set<String> keys = splitKeys.stream().map(Object::toString).collect(java.util.stream.Collectors.toSet());

        assertTrue(keys.stream().anyMatch(k -> k.startsWith("Read<T<A<X>,B<Y>>>+")));
        assertTrue(keys.stream().anyMatch(k -> k.startsWith("Read<T<A<X>,B<?>>>+")));
        assertTrue(keys.stream().anyMatch(k -> k.startsWith("Read<T<A<X>,B>>+")));
        assertTrue(keys.stream().anyMatch(k -> k.startsWith("Read<T<A<?>,B<Y>>>+")));
        assertTrue(keys.stream().anyMatch(k -> k.startsWith("Read<T<A<?>,B<?>>>+")));
        assertTrue(keys.stream().anyMatch(k -> k.startsWith("Read<T<A<?>,B>>+")));
        assertTrue(keys.stream().anyMatch(k -> k.startsWith("Read<T<A,B<Y>>>+")));
        assertTrue(keys.stream().anyMatch(k -> k.startsWith("Read<T<A,B<?>>>+")));
        assertTrue(keys.stream().anyMatch(k -> k.startsWith("Read<T<A,B>>+")));
        assertTrue(keys.stream().allMatch(k -> k.startsWith("Read<")));
    }

    @Test
    void splitRuntimeKeyPreservesFullEventNames()
    {
        EventSplitter splitter = new EventSplitter();
        String oldValue = new String("old");
        String newValue = new String("new");
        Transit<String> transit = new Transit<>(oldValue, newValue);
        TypeKey<String> typeKey = TypeKey.valueOf(String.class, true);
        RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(transit, typeKey, newValue, true);

        Set<RuntimeKey<String>> splitKeys = splitter.split(runtimeKey, true);

        assertTrue(splitKeys.stream()
                .allMatch(key -> key.toString().startsWith("com.taitl.existential.events")));
    }

    @Test
    void splitRejectsRuntimeKeyWithoutEvent()
    {
        EventSplitter splitter = new EventSplitter();
        RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(String.class, "String", "value", false);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> splitter.split(runtimeKey));

        assertEquals("Argument 'event' must not be null", error.getMessage());
    }

    @Test
    void splitTransitRejectsNullTransitWithEventMessage()
    {
        TestEventSplitter splitter = new TestEventSplitter();

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> splitter.splitTransitPublic(null, new LinkedHashSet<>()));

        assertEquals("Argument 'event' must not be null", error.getMessage());
    }

    @Test
    void splitTransitEmitsCombinedEventsForCreateUpdateDelete()
    {
        EventSplitter splitter = new EventSplitter();

        Set<Event<String>> createdEvents = splitter.splitEvent(new Transit<>(null, "new"));
        assertTrue(hasEvent(createdEvents, CU.class));
        assertTrue(hasEvent(createdEvents, CUD.class));
        assertFalse(hasEvent(createdEvents, UD.class));

        Set<Event<String>> updatedEvents = splitter.splitEvent(new Transit<>("old", "new"));
        assertTrue(hasEvent(updatedEvents, CU.class));
        assertTrue(hasEvent(updatedEvents, UD.class));
        assertTrue(hasEvent(updatedEvents, CUD.class));

        Set<Event<String>> deletedEvents = splitter.splitEvent(new Transit<>("old", null));
        assertTrue(hasEvent(deletedEvents, UD.class));
        assertTrue(hasEvent(deletedEvents, CUD.class));
    }
}
