package com.taitl.existential.handlers;

import com.taitl.ex.examples.night_city.model.Cat;
import com.taitl.existential.exceptions.EventHandlerExecutionException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OnMutateTest
{
    @Test
    void conditionOnlyRejectsWhenFalse()
    {
        OnMutate<Cat> handler = new OnMutate<>(c -> "Black".equals(c.color), null, "Cats are black");
        Cat before = new Cat("Black", "Park");
        Cat after = new Cat("White", "Park");

        EventHandlerExecutionException ex = assertThrows(EventHandlerExecutionException.class,
                () -> handler.handle(before, after));

        assertThat(ex.getMessage(), containsString("Cats are black"));
    }

    @Test
    void conditionOnlyAllowsWhenTrue()
    {
        OnMutate<Cat> handler = new OnMutate<>(c -> "Black".equals(c.color), null, "Cats are black");
        Cat before = new Cat("Black", "Park");
        Cat after = new Cat("Black", "Park");

        assertDoesNotThrow(() -> handler.handle(before, after));
    }

    @Test
    void biconditionOnlyRejectsWhenFalse()
    {
        OnMutate<Cat> handler = new OnMutate<>((c0, c1) -> c0.color.equals(c1.color), null, "Colors must match");
        Cat before = new Cat("Black", "Park");
        Cat after = new Cat("White", "Park");

        EventHandlerExecutionException ex = assertThrows(EventHandlerExecutionException.class,
                () -> handler.handle(before, after));

        assertThat(ex.getMessage(), containsString("Colors must match"));
    }
}
