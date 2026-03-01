package com.taitl.existential.events;

import com.taitl.existential.Existential;
import com.taitl.existential.events.types.BiEvent;
import com.taitl.existential.events.types.Event;
import com.taitl.existential.keys.TypeKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExistentialEventArgsTest
{
    @Test
    @DisplayName("Event args - null event reports event name (single)")
    void nullEventReportsEventNameSingle()
    {
        Existential ex = new Existential();
        try
        {
            IllegalArgumentException err = assertThrows(IllegalArgumentException.class,
                    () -> ex.event((Event<String>) null, "value", new TypeKey<>(String.class), "tran"));
            assertEquals("Argument 'event' must not be null", err.getMessage());
        }
        finally
        {
            ex.close();
        }
    }

    @Test
    @DisplayName("Event args - null event reports event name (bi)")
    void nullEventReportsEventNameBi()
    {
        Existential ex = new Existential();
        try
        {
            IllegalArgumentException err = assertThrows(IllegalArgumentException.class,
                    () -> ex.event((BiEvent<String>) null, new TypeKey<>(String.class), "tran"));
            assertEquals("Argument 'event' must not be null", err.getMessage());
        }
        finally
        {
            ex.close();
        }
    }
}
