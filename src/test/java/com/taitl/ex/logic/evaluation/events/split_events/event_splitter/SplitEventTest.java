package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.evaluation.events.split_events.*;
import com.taitl.ex.logic.evaluation.events.split_events.data.*;
import com.taitl.existential.events.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SplitEventTest
{
    ConfigurationIndexes indexes;
    SplitEvent splitEvent;

    @BeforeEach
    void setup()
    {
        indexes = new ConfigurationIndexes();
        indexes.doneIndexing();
        splitEvent = new SplitEvent();
    }

    @Nested
    class Call
    {
        @Test
        @DisplayName("Stores original event")
        void storesOriginalEvent()
        {
            String oldValue = new String("old");
            Port<String> port = new Port<>(oldValue, null);
            RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(port, new TypeKey<>(String.class), null, false);

            SplitResult<String> result = assertDoesNotThrow(
                    () -> splitEvent.call(runtimeKey, indexes.eventField(), false, true, null));

            assertSame(port, result.event());
            assertTrue(result.evaluables().isEmpty());
        }
    }
}
