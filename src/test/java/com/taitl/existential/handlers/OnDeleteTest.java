package com.taitl.existential.handlers;

import com.taitl.ex.examples.night_city.model.Cat;
import com.taitl.existential.events.Delete;
import com.taitl.existential.events.types.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OnDeleteTest
{
    @Nested
    class Handle
    {
        @Test
        @DisplayName("Condition prevents action when false")
        void conditionPreventsAction() throws Exception
        {
            AtomicInteger calls = new AtomicInteger();
            OnDelete<Cat> handler = new OnDelete<>(c -> "Black".equals(c.color), c -> calls.incrementAndGet());
            Cat cat = new Cat("White", "Park");

            handler.handle(cat);
            assertEquals(0, calls.get());

            cat.color = "Black";
            handler.handle(cat);
            assertEquals(1, calls.get());
        }
    }

    @Nested
    class EventTypeMapping
    {
        @Test
        @DisplayName("Maps to Delete event type")
        void mapsToDeleteEventType()
        {
            OnDelete<Cat> handler = new OnDelete<>(c -> {
            }, "delete");
            assertEquals(EventType.valueOf(Delete.class), handler.eventType());
        }
    }
}
