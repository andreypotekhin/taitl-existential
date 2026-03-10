package com.taitl.existential.handlers;

import com.taitl.ex.examples.night_city.model.Cat;
import com.taitl.existential.events.Create;
import com.taitl.existential.events.types.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OnCreateTest
{
    @Nested
    class Handle
    {
        @Test
        @DisplayName("Condition prevents action when false")
        void conditionPreventsAction() throws Exception
        {
            AtomicInteger calls = new AtomicInteger();
            OnCreate<Cat> handler = new OnCreate<>(c -> "Black".equals(c.color), c -> calls.incrementAndGet());
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
        @DisplayName("Maps to Create event type")
        void mapsToCreateEventType()
        {
            OnCreate<Cat> handler = new OnCreate<>(c -> {
            }, "create");
            assertEquals(EventType.valueOf(Create.class), handler.eventType());
        }
    }
}
