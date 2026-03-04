package com.taitl.ex.logic.evaluation.actions;

import com.taitl.ex.logic.evaluation.events.actions.*;
import com.taitl.ex.logic.stages.validation.output.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.quantifiers.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class ExecuteHandlersTest
{
    ExecuteHandlers executeHandlers;
    ValidationReport report;

    @BeforeEach
    void setup()
    {
        executeHandlers = new ExecuteHandlers();
        report = new ValidationReport();
    }

    @Nested
    class Call
    {
        @Nested
        class Payload
        {
            @Test
            @DisplayName("Uses original event payload")
            void originalEventPayload() throws Exception
            {
                String oldValue = new String("old");
                String newValue = new String("new");
                Port<String> port = new Port<>(oldValue, newValue);
                AtomicInteger unaryCalls = new AtomicInteger();
                AtomicInteger biCalls = new AtomicInteger();

                List<Ev<String>> evs = List.of(
                        new OnUpdate<String>(null, value -> {
                            unaryCalls.incrementAndGet();
                            assertSame(newValue, value);
                        }),
                        new OnTransit<String>((t0, t1) -> {
                            biCalls.incrementAndGet();
                            assertSame(oldValue, t0);
                            assertSame(newValue, t1);
                        }),
                        new OnPort<String>((t0, t1) -> {
                            biCalls.incrementAndGet();
                            assertSame(oldValue, t0);
                            assertSame(newValue, t1);
                        }));

                executeHandlers.call(evs, port, report);

                assertEquals(1, unaryCalls.get());
                assertEquals(2, biCalls.get());
                assertTrue(report.exceptions().isEmpty());
            }

            @Test
            @DisplayName("Adapts bi handler payload for elementary create event")
            void adaptedForCreate() throws Exception
            {
                String entity = new String("new");
                AtomicInteger calls = new AtomicInteger();

                List<Ev<String>> evs = List.of(
                        new OnPort<String>((t0, t1) -> {
                            calls.incrementAndGet();
                            assertSame(entity, t0);
                            assertSame(entity, t1);
                        }),
                        new OnTransit<String>((t0, t1) -> {
                            calls.incrementAndGet();
                            assertSame(entity, t0);
                            assertSame(entity, t1);
                        }));

                executeHandlers.call(evs, new Create<>(entity), report);

                assertEquals(2, calls.get());
                assertTrue(report.exceptions().isEmpty());
            }
        }

        @Nested
        class Expressions
        {
            @Test
            @DisplayName("Evaluates all expression")
            void allExpression() throws Exception
            {
                AtomicInteger calls = new AtomicInteger();

                List<Ev<String>> evs = List.of(new All<String>(value -> {
                    calls.incrementAndGet();
                    return value.startsWith("n");
                }, "value should start with 'n'"));

                executeHandlers.call(evs, new Create<>("new"), report);

                assertEquals(1, calls.get());
                assertTrue(report.exceptions().isEmpty());
            }

            @Test
            @DisplayName("Evaluates invariant all and collects predicate failure")
            void invariantAll() throws Exception
            {
                AtomicInteger calls = new AtomicInteger();

                Invariant<String> invariant = new Invariant<>(String.class);
                invariant.all(value -> {
                    calls.incrementAndGet();
                    return value.startsWith("n");
                }, "value should start with 'n'");

                List<Ev<String>> evs = new ArrayList<>();
                evs.addAll(invariant.list());

                executeHandlers.call(evs, new Create<>("old"), report);

                assertEquals(1, calls.get());
                assertEquals(1, report.exceptions().size());
                assertInstanceOf(PredicateFailure.class, report.exceptions().get(0));
            }
        }

        @Nested
        class Rejects
        {
            @Test
            @DisplayName("Rejects bi handler for non transition single events")
            void biHandlerForRead()
            {
                IllegalStateException error = assertThrows(IllegalStateException.class,
                        () -> executeHandlers.call(List.of(new OnPort<String>((t0, t1) -> {
                        })), new Read<>("v"), report));

                assertTrue(error.getMessage().contains("Bi-event handler requires BiEvent runtime event"));
            }
        }
    }
}
