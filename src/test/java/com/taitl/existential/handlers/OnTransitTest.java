package com.taitl.existential.handlers;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.exceptions.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class OnTransitTest
{
    Cat cat(String color)
    {
        return new Cat(color, "Park");
    }

    @Nested
    class ConditionOnly
    {
        @Test
        @DisplayName("Rejects when false")
        void rejectsWhenFalse()
        {
            OnTransit<Cat> handler = new OnTransit<>(c -> "Black".equals(c.color), null, "Cats are black");

            EventHandlerException ex = assertThrows(EventHandlerException.class,
                    () -> handler.handle(cat("Black"), cat("White")));

            assertThat(ex.getMessage(), containsString("Cats are black"));
        }

        @Test
        @DisplayName("Allows when true")
        void allowsWhenTrue()
        {
            OnTransit<Cat> handler = new OnTransit<>(c -> "Black".equals(c.color), null, "Cats are black");
            assertDoesNotThrow(() -> handler.handle(cat("Black"), cat("Black")));
        }

        @Test
        @DisplayName("Bicondition rejects when false")
        void biconditionRejectsWhenFalse()
        {
            OnTransit<Cat> handler =
                    new OnTransit<>((c0, c1) -> c0.color.equals(c1.color), null, "Colors must match");

            EventHandlerException ex = assertThrows(EventHandlerException.class,
                    () -> handler.handle(cat("Black"), cat("White")));

            assertThat(ex.getMessage(), containsString("Colors must match"));
        }
    }
}
