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
    Cat cat(String color)
    {
        return new Cat(color, "Park");
    }

    @Nested
    class Conditions
    {
        @Test
        @DisplayName("Condition only rejects when false")
        void conditionRejectsWhenFalse()
        {
            OnPort<Cat> handler = new OnPort<>(c -> "Black".equals(c.color), null, "Cats are black");
            var ex = assertThrows(EventHandlerException.class, () -> handler.handle(cat("Black"), cat("White")));
            assertThat(ex.getMessage(), containsString("Cats are black"));
        }

        @Test
        @DisplayName("Condition only allows when true")
        void conditionAllowsWhenTrue()
        {
            OnPort<Cat> handler = new OnPort<>(c -> "Black".equals(c.color), null, "Cats are black");
            assertDoesNotThrow(() -> handler.handle(cat("Black"), cat("Black")));
        }

        @Test
        @DisplayName("Bicondition only rejects when false")
        void biconditionRejectsWhenFalse()
        {
            OnPort<Cat> handler = new OnPort<>((c0, c1) -> c0.color.equals(c1.color), null, "Colors must match");
            var ex = assertThrows(EventHandlerException.class, () -> handler.handle(cat("Black"), cat("White")));
            assertThat(ex.getMessage(), containsString("Colors must match"));
        }

        @Test
        @DisplayName("Handle rejects both nulls")
        void rejectsBothNulls()
        {
            OnPort<Cat> handler = new OnPort<>((t0, t1) -> {
            });

            var ex = assertThrows(IllegalArgumentException.class, () -> handler.handle(null, null));

            assertThat(ex.getMessage(), containsString("Arguments 't0' and 't1' should not be both null"));
        }
    }

    @Nested
    class Handle
    {
        @Test
        @DisplayName("Runs action when no condition provided")
        void runsActionWithoutCondition() throws Exception
        {
            AtomicInteger counter = new AtomicInteger();
            OnPort<Integer> on = new OnPort<>((t0, t1) -> counter.incrementAndGet());

            on.handle(1, 2);

            assertThat(counter.get(), is(1));
        }

        @Test
        @DisplayName("Skips action when predicate fails")
        void skipsActionWhenPredicateFails() throws Exception
        {
            AtomicInteger counter = new AtomicInteger();
            OnPort<Integer> on = new OnPort<>(t1 -> t1 > 10, (t0, t1) -> counter.incrementAndGet());

            on.handle(1, 2);

            assertThat(counter.get(), is(0));
        }

        @Test
        @DisplayName("Runs action when bi predicate passes")
        void runsActionWhenBiPredicatePasses() throws Exception
        {
            AtomicInteger counter = new AtomicInteger();
            OnPort<Integer> on = new OnPort<>((t0, t1) -> t0 < t1, (t0, t1) -> counter.incrementAndGet());

            on.handle(1, 2);

            assertThat(counter.get(), is(1));
        }
    }
}
