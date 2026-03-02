package com.taitl.existential.handlers;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.exceptions.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class OnPortTest
{
    @Test
    @DisplayName("Condition only rejects when false")
    void conditionOnlyRejectsWhenFalse()
    {
        OnPort<Cat> handler = new OnPort<>(c -> "Black".equals(c.color), null, "Cats are black");
        Cat before = new Cat("Black", "Park");
        Cat after = new Cat("White", "Park");

        var ex = assertThrows(EventHandlerException.class,
                () -> handler.handle(before, after));

        assertThat(ex.getMessage(), containsString("Cats are black"));
    }

    @Test
    @DisplayName("Condition only allows when true")
    void conditionOnlyAllowsWhenTrue()
    {
        OnPort<Cat> handler = new OnPort<>(c -> "Black".equals(c.color), null, "Cats are black");
        Cat before = new Cat("Black", "Park");
        Cat after = new Cat("Black", "Park");

        assertDoesNotThrow(() -> handler.handle(before, after));
    }

    @Test
    @DisplayName("Bicondition only rejects when false")
    void biconditionOnlyRejectsWhenFalse()
    {
        OnPort<Cat> handler = new OnPort<>((c0, c1) -> c0.color.equals(c1.color), null, "Colors must match");
        Cat before = new Cat("Black", "Park");
        Cat after = new Cat("White", "Park");

        var ex = assertThrows(EventHandlerException.class,
                () -> handler.handle(before, after));

        assertThat(ex.getMessage(), containsString("Colors must match"));
    }

    @Test
    @DisplayName("Handle rejects both nulls")
    void handleRejectsBothNulls()
    {
        OnPort<Cat> handler = new OnPort<>((t0, t1) -> {
        });

        var ex = assertThrows(IllegalArgumentException.class,
                () -> handler.handle(null, null));

        assertThat(ex.getMessage(), containsString("Arguments 't0' and 't1' should not be both null"));
    }

    @Test
    @DisplayName("Handle runs action when no condition provided")
    void handleRunsActionWhenNoConditionProvided() throws Exception
    {
        AtomicInteger counter = new AtomicInteger();
        OnPort<Integer> on = new OnPort<>((t0, t1) -> counter.incrementAndGet());

        on.handle(1, 2);

        assertThat(counter.get(), is(1));
    }

    @Test
    @DisplayName("Handle skips action when predicate fails")
    void handleSkipsActionWhenPredicateFails() throws Exception
    {
        AtomicInteger counter = new AtomicInteger();
        OnPort<Integer> on = new OnPort<>(t1 -> t1 > 10, (t0, t1) -> counter.incrementAndGet());

        on.handle(1, 2);

        assertThat(counter.get(), is(0));
    }

    @Test
    @DisplayName("Handle runs action when bi predicate passes")
    void handleRunsActionWhenBiPredicatePasses() throws Exception
    {
        AtomicInteger counter = new AtomicInteger();
        OnPort<Integer> on = new OnPort<>((t0, t1) -> t0 < t1, (t0, t1) -> counter.incrementAndGet());

        on.handle(1, 2);

        assertThat(counter.get(), is(1));
    }
}
