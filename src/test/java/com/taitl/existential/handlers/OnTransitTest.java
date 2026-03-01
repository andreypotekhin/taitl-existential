package com.taitl.existential.handlers;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.exceptions.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class OnTransitTest
{
    @Test
    void conditionOnlyRejectsWhenFalse()
    {
        OnTransit<Cat> handler = new OnTransit<>(c -> "Black".equals(c.color), null, "Cats are black");
        Cat before = new Cat("Black", "Park");
        Cat after = new Cat("White", "Park");

        var ex = assertThrows(EventHandlerException.class,
                () -> handler.handle(before, after));

        assertThat(ex.getMessage(), containsString("Cats are black"));
    }

    @Test
    void conditionOnlyAllowsWhenTrue()
    {
        OnTransit<Cat> handler = new OnTransit<>(c -> "Black".equals(c.color), null, "Cats are black");
        Cat before = new Cat("Black", "Park");
        Cat after = new Cat("Black", "Park");

        assertDoesNotThrow(() -> handler.handle(before, after));
    }

    @Test
    void biconditionOnlyRejectsWhenFalse()
    {
        OnTransit<Cat> handler = new OnTransit<>((c0, c1) -> c0.color.equals(c1.color), null, "Colors must match");
        Cat before = new Cat("Black", "Park");
        Cat after = new Cat("White", "Park");

        var ex = assertThrows(EventHandlerException.class,
                () -> handler.handle(before, after));

        assertThat(ex.getMessage(), containsString("Colors must match"));
    }

    @Test
    void handleRejectsBothNulls()
    {
        OnTransit<Cat> handler = new OnTransit<>((t0, t1) -> {
        });

        var ex = assertThrows(IllegalArgumentException.class,
                () -> handler.handle(null, null));

        assertThat(ex.getMessage(), containsString("Arguments 't0' and 't1' should not be both null"));
    }

    @Test
    void handleRunsActionWhenNoConditionProvided() throws Exception
    {
        AtomicInteger counter = new AtomicInteger();
        OnTransit<Integer> on = new OnTransit<>((t0, t1) -> counter.incrementAndGet());

        on.handle(1, 2);

        assertThat(counter.get(), is(1));
    }

    @Test
    void handleSkipsActionWhenPredicateFails() throws Exception
    {
        AtomicInteger counter = new AtomicInteger();
        OnTransit<Integer> on = new OnTransit<>(t1 -> t1 > 10, (t0, t1) -> counter.incrementAndGet());

        on.handle(1, 2);

        assertThat(counter.get(), is(0));
    }

    @Test
    void handleRunsActionWhenBiPredicatePasses() throws Exception
    {
        AtomicInteger counter = new AtomicInteger();
        OnTransit<Integer> on = new OnTransit<>((t0, t1) -> t0 < t1, (t0, t1) -> counter.incrementAndGet());

        on.handle(1, 2);

        assertThat(counter.get(), is(1));
    }
}
