package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.existential.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.transactions.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EventSplitterTest
{
    protected Existential ex;

    @BeforeEach
    void setup()
    {
        ex = new Existential();
        ex.configure().context("/memo").effect(String.class).create(value -> {
        }, "noop");
    }

    @AfterEach
    void cleanup()
    {
        ex.close();
    }

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
        class ElementaryCases
        {
            @Test
            @DisplayName("Split create emits compound families by default")
            void createEmitsCompoundByDefault()
            {
                EventSplitter splitter = new EventSplitter();
                String entity = new String("new");

                Set<Event<String>> events = splitter.splitEvent(new Create<>(entity));

                assertTrue(hasEvent(events, Create.class));
                assertTrue(hasEvent(events, CU.class));
                assertTrue(hasEvent(events, CUD.class));
                assertTrue(hasEvent(events, Port.class));
            }

            @Test
            @DisplayName("Split create can disable elementary to compound split")
            void disableElementaryToCompound()
            {
                EventSplitter splitter = new EventSplitter();

                Set<Event<String>> events = splitter.splitEvent(new Create<>("new"), false);

                assertEquals(1, events.size());
                assertTrue(hasEvent(events, Create.class));
                assertFalse(hasEvent(events, CU.class));
                assertFalse(hasEvent(events, CUD.class));
                assertFalse(hasEvent(events, Port.class));
            }
        }

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
            void byUnderlyingEvent() throws Exception
            {
                EventSplitter splitter = new EventSplitter();
                String oldValue = new String("old");
                String newValue = new String("new");
                TypeKey<String> typeKey = new TypeKey<>(String.class);
                RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(new Port<>(oldValue, newValue), typeKey, newValue,
                        false);

                Set<RuntimeKey<String>> splitKeys = splitter.split(runtimeKey, false, true, null);

                assertTrue(splitKeys.stream().anyMatch(key -> key.toString().startsWith("Transit<String>+")));
                assertTrue(splitKeys.stream().allMatch(key -> key.typeKey().toString().equals("String")));
            }

            @Test
            @DisplayName("Split runtime key also splits type key by generics dimension")
            void byGenerics() throws Exception
            {
                EventSplitter splitter = new EventSplitter();
                RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(
                        new Read<>("value"),
                        TypeKey.valueOf("T<A<X>,B<Y>>"),
                        "value",
                        false);

                Set<RuntimeKey<String>> splitKeys = splitter.split(runtimeKey, false, true, null);
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
            void preservesFullEventNames() throws Exception
            {
                EventSplitter splitter = new EventSplitter();
                String oldValue = new String("old");
                String newValue = new String("new");
                Port<String> port = new Port<>(oldValue, newValue);
                TypeKey<String> typeKey = TypeKey.valueOf(String.class, true);
                RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(port, typeKey, newValue, true);

                Set<RuntimeKey<String>> splitKeys = splitter.split(runtimeKey, true, true, null);

                assertTrue(splitKeys.stream()
                        .allMatch(key -> key.toString().startsWith("com.taitl.existential.events")));
            }

            @Test
            @DisplayName("Split runtime key for create can disable elementary to compound mapping")
            void createWithoutCompound() throws Exception
            {
                EventSplitter splitter = new EventSplitter();
                String entity = new String("new");
                TypeKey<String> typeKey = new TypeKey<>(String.class);
                RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(new Create<>(entity), typeKey, entity, false);

                Set<RuntimeKey<String>> splitKeys = splitter.split(runtimeKey, false, false, null);

                assertEquals(1, splitKeys.size());
                assertTrue(splitKeys.stream().allMatch(key -> key.key().toString().startsWith("Create<")));
            }

            @Test
            @DisplayName("Runtime split uses memo-backed transit payload for update")
            void usesMemoBackedTransitPayload() throws Exception
            {
                EventSplitter splitter = new EventSplitter();
                String live = new String("after");
                String before = new String("before");
                TypeKey<String> typeKey = new TypeKey<>(String.class);
                Tr tr = ex.begin("/memo");
                tr.memo(before, live, typeKey);
                RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(new Update<>(live), typeKey, live, false);

                Set<RuntimeKey<String>> splitKeys = splitter.split(runtimeKey, false, true, tr);

                Transit<String> transit = splitKeys.stream()
                        .map(RuntimeKey::event)
                        .filter(Transit.class::isInstance)
                        .map(event -> (Transit<String>) event)
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Transit event not found"));

                assertSame(before, transit.t0);
                assertSame(live, transit.t1);
                tr.rollback();
            }

            @Test
            @DisplayName("Runtime split skips memo-sensitive bi-event path when memo is absent")
            void skipsBiEventPathWithoutMemo() throws Exception
            {
                EventSplitter splitter = new EventSplitter();
                String live = new String("after");
                TypeKey<String> typeKey = new TypeKey<>(String.class);
                Tr tr = ex.begin("/memo");
                RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(new Update<>(live), typeKey, live, false);

                Set<RuntimeKey<String>> splitKeys = splitter.split(runtimeKey, false, true, tr);

                assertFalse(splitKeys.stream().anyMatch(key -> key.event() instanceof Transit<?>));
                tr.rollback();
            }
        }

        @Nested
        class Rejects
        {
            @Test
            @DisplayName("Split rejects runtime key without event")
            void missingEvent()
            {
                EventSplitter splitter = new EventSplitter();
                RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(String.class, "String", "value", false);

                IllegalArgumentException error =
                        assertThrows(IllegalArgumentException.class,
                                () -> splitter.split(runtimeKey, true, true, null));

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
            void nullTransit()
            {
                TestEventSplitter splitter = new TestEventSplitter();

                IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                        () -> splitter.splitTransitPublic(null, new LinkedHashSet<>()));

                assertEquals("Argument 'event' must not be null", error.getMessage());
            }
        }
    }
}
