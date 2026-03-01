package com.taitl.existential.handlers;

import java.util.concurrent.atomic.AtomicInteger;
import com.taitl.ex.examples.night_city.model.Cat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OnUpdateTest
{
    @Test
    @DisplayName("Condition prevents action when false")
    void conditionPreventsActionWhenFalse() throws Exception
    {
        AtomicInteger calls = new AtomicInteger();
        OnUpdate<Cat> handler = new OnUpdate<>(c -> "Black".equals(c.color), c -> calls.incrementAndGet());
        Cat cat = new Cat("White", "Park");

        handler.handle(cat);
        assertEquals(0, calls.get());

        cat.color = "Black";
        handler.handle(cat);
        assertEquals(1, calls.get());
    }
}
