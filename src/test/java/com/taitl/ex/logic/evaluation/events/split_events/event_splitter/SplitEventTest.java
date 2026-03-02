package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.evaluation.events.split_events.*;
import com.taitl.existential.events.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SplitEventTest
{
    @Test
    @DisplayName("Call stores original event")
    void callStoresOriginalEvent()
    {
        ConfigurationIndexes indexes = new ConfigurationIndexes();
        indexes.doneIndexing();

        SplitEvent splitEvent = new SplitEvent();
        String oldValue = new String("old");
        Port<String> port = new Port<>(oldValue, null);
        RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(port, new TypeKey<>(String.class), null, false);

        SplitResult result = splitEvent.call(runtimeKey, indexes.eventField(), false);

        assertSame(port, result.event());
        assertTrue(result.evaluables().isEmpty());
    }
}
