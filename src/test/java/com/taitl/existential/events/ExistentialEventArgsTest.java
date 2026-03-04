package com.taitl.existential.events;

import com.taitl.existential.Existential;
import com.taitl.existential.events.types.BiEvent;
import com.taitl.existential.events.types.Event;
import com.taitl.existential.keys.TypeKey;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExistentialEventArgsTest
{
    Existential ex;

    @BeforeEach
    void setup()
    {
        ex = new Existential();
    }

    @AfterEach
    void cleanup()
    {
        ex.close();
    }

    @Nested
    class NullEvent
    {
        @Test
        @DisplayName("Reports event name for single event")
        void single()
        {
            IllegalArgumentException err = assertThrows(IllegalArgumentException.class,
                    () -> ex.event((Event<String>) null, "value", new TypeKey<>(String.class), "tran"));
            assertEquals("Argument 'event' must not be null", err.getMessage());
        }

        @Test
        @DisplayName("Reports event name for bi event")
        void bi()
        {
            IllegalArgumentException err = assertThrows(IllegalArgumentException.class,
                    () -> ex.event((BiEvent<String>) null, new TypeKey<>(String.class), "tran"));
            assertEquals("Argument 'event' must not be null", err.getMessage());
        }
    }
}
