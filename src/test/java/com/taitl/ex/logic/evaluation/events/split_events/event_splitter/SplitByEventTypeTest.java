package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SplitByEventTypeTest
{
    SplitByEventType splitter;

    @BeforeEach
    void setUp()
    {
        splitter = new SplitByEventType();
    }

    @Test
    @DisplayName("Split transit rejects null events with events message")
    void splitTransitRejectsNullEventsWithEventsMessage()
    {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> splitter.splitTransit(new Transit<>("old", "new"), null));
        assertEquals("Argument 'events' must not be null", error.getMessage());
    }

    @Test
    @DisplayName("Split read and lock rejects null events with events message")
    void splitReadAndLockRejectsNullEventsWithEventsMessage()
    {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> splitter.splitReadAndLock(new ReadAndLock<>("value"), null));
        assertEquals("Argument 'events' must not be null", error.getMessage());
    }
}
