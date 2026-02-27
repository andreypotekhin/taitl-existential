package com.taitl.ex.logic.evaluation.actions;

import com.taitl.ex.logic.validation.output.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.*;
import com.taitl.existential.handlers.*;
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
}
