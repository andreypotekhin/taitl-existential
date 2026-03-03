package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

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
        private <T> Set<Event<T>> splitTransitPublic(Port<T> port, Set<Event<T>> set)
        {
            return splitTransit(port, set);
        }
    }

    private static <T> boolean hasEvent(Set<Event<T>> events, Class<?> eventClass)
    {
        return events.stream().anyMatch(eventClass::isInstance);
    }

    @Nested
    class SplitEvent
    {
        @Nested
        class PortCases
        {
            @Test
            @DisplayName("Split transit uses updated entity for update")
            void updatedEntityForUpdate()
            {
                EventSplitter splitter = new EventSplitter();
                String oldValue = new String("old");
                String newValue = new String("new");

                Set<Event<String>> events = splitter.splitEvent(new Port<>(oldValue, newValue));

                Update<String> update = events.stream()
                        .filter(event -> event instanceof Update<?>)
                        .map(event -> (Update<String>) event)
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Update event not found"));

                assertSame(newValue, update.t);
            }

            @Test
            @DisplayName("Split transit emits combined events for create update delete")
            void combinedCreateUpdateDelete()
            {
                EventSplitter splitter = new EventSplitter();

                Set<Event<String>> createdEvents = splitter.splitEvent(new Port<>(null, "new"));
                assertTrue(hasEvent(createdEvents, CU.class));
                assertTrue(hasEvent(createdEvents, CUD.class));
                assertFalse(hasEvent(createdEvents, UD.class));

                Set<Event<String>> updatedEvents = splitter.splitEvent(new Port<>("old", "new"));
                assertTrue(hasEvent(updatedEvents, CU.class));
                assertTrue(hasEvent(updatedEvents, UD.class));
                assertTrue(hasEvent(updatedEvents, CUD.class));

                Set<Event<String>> deletedEvents = splitter.splitEvent(new Port<>("old", null));
                assertTrue(hasEvent(deletedEvents, UD.class));
                assertTrue(hasEvent(deletedEvents, CUD.class));
            }
        }
    }

    @Nested
    class Split
    {
        @Nested
        class RuntimeKeyCases
        {
            @Test
            @DisplayName("Split runtime key splits by underlying event and keeps type key")
            void byUnderlyingEventKeepsTypeKey()
            {
                EventSplitter splitter = new EventSplitter();
                String oldValue = new String("old");
                String newValue = new String("new");
                TypeKey<String> typeKey = new TypeKey<>(String.class);
                RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(new Port<>(oldValue, newValue), typeKey, newValue,
                        false);

                Set<RuntimeKey<String>> splitKeys = splitter.split(runtimeKey);

                assertTrue(splitKeys.stream().anyMatch(key -> key.toString().startsWith("Transit<String>+")));
                assertTrue(splitKeys.stream().allMatch(key -> key.typeKey().toString().equals("String")));
            }

            @Test
            @DisplayName("Split runtime key also splits type key by generics dimension")
            void byGenericsDimension()
            {
                EventSplitter splitter = new EventSplitter();
                RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(
                        new Read<>("value"),
                        TypeKey.valueOf("T<A<X>,B<Y>>"),
                        "value",
                        false);

                Set<RuntimeKey<String>> splitKeys = splitter.split(runtimeKey);
                Set<String> keys =
                        splitKeys.stream().map(Object::toString).collect(java.util.stream.Collectors.toSet());

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
            @DisplayName("Split runtime key preserves full event names")
            void preservesFullEventNames()
            {
                EventSplitter splitter = new EventSplitter();
                String oldValue = new String("old");
                String newValue = new String("new");
                Port<String> port = new Port<>(oldValue, newValue);
                TypeKey<String> typeKey = TypeKey.valueOf(String.class, true);
                RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(port, typeKey, newValue, true);

                Set<RuntimeKey<String>> splitKeys = splitter.split(runtimeKey, true);

                assertTrue(splitKeys.stream()
                        .allMatch(key -> key.toString().startsWith("com.taitl.existential.events")));
            }
        }

        @Nested
        class Rejects
        {
            @Test
            @DisplayName("Split rejects runtime key without event")
            void runtimeKeyWithoutEvent()
            {
                EventSplitter splitter = new EventSplitter();
                RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(String.class, "String", "value", false);

                IllegalArgumentException error =
                        assertThrows(IllegalArgumentException.class, () -> splitter.split(runtimeKey));

                assertEquals("Argument 'event' must not be null", error.getMessage());
            }
        }
    }

    @Nested
    class SplitPort
    {
        @Nested
        class Rejects
        {
            @Test
            @DisplayName("Split transit rejects null transit with event message")
            void nullTransitWithEventMessage()
            {
                TestEventSplitter splitter = new TestEventSplitter();

                IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                        () -> splitter.splitTransitPublic(null, new LinkedHashSet<>()));

                assertEquals("Argument 'event' must not be null", error.getMessage());
            }
        }
    }
}
