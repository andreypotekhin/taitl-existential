package com.taitl.ex.logic.evaluation.actions;

import com.taitl.ex.logic.validation.output.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.quantifiers.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class ExecuteHandlersTest
{
    @Test
    void callUsesOriginalEventPayload() throws Exception
    {
        ExecuteHandlers executeHandlers = new ExecuteHandlers();
        ValidationReport report = new ValidationReport();
        String oldValue = new String("old");
        String newValue = new String("new");
        Transit<String> transit = new Transit<>(oldValue, newValue);
        AtomicInteger unaryCalls = new AtomicInteger();
        AtomicInteger biCalls = new AtomicInteger();

        List<Ev<?>> evs = List.of(
                new OnUpdate<String>(null, value -> {
                    unaryCalls.incrementAndGet();
                    assertSame(newValue, value);
                }),
                new OnMutate<String>((t0, t1) -> {
                    biCalls.incrementAndGet();
                    assertSame(oldValue, t0);
                    assertSame(newValue, t1);
                }),
                new OnTransit<String>((t0, t1) -> {
                    biCalls.incrementAndGet();
                    assertSame(oldValue, t0);
                    assertSame(newValue, t1);
                }));

        executeHandlers.call(evs, transit, report);

        assertEquals(1, unaryCalls.get());
        assertEquals(2, biCalls.get());
        assertTrue(report.exceptions().isEmpty());
    }

    @Test
    void callEvaluatesAllExpression() throws Exception
    {
        ExecuteHandlers executeHandlers = new ExecuteHandlers();
        ValidationReport report = new ValidationReport();
        AtomicInteger calls = new AtomicInteger();

        List<Ev<?>> evs = List.of(new All<String>(value -> {
            calls.incrementAndGet();
            return value.startsWith("n");
        }, "value should start with 'n'"));

        executeHandlers.call(evs, new Create<>("new"), report);

        assertEquals(1, calls.get());
        assertTrue(report.exceptions().isEmpty());
    }

    @Test
    void callEvaluatesInvariantAllAndCollectsPredicateFailure() throws Exception
    {
        ExecuteHandlers executeHandlers = new ExecuteHandlers();
        ValidationReport report = new ValidationReport();
        AtomicInteger calls = new AtomicInteger();

        Invariant<String> invariant = new Invariant<>(String.class);
        invariant.all(value -> {
            calls.incrementAndGet();
            return value.startsWith("n");
        }, "value should start with 'n'");

        List<Ev<?>> evs = new ArrayList<>();
        evs.addAll(invariant.list());

        executeHandlers.call(evs, new Create<>("old"), report);

        assertEquals(1, calls.get());
        assertEquals(1, report.exceptions().size());
        assertInstanceOf(PredicateFailure.class, report.exceptions().get(0));
    }
}
