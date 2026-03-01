package com.taitl.ex.logic.evaluation.split_events;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.events.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SplitEventTest
{
    @Test
    void callStoresOriginalEvent()
    {
        ConfigurationIndexes indexes = new ConfigurationIndexes();
        indexes.doneIndexing();

        SplitEvent splitEvent = new SplitEvent();
        String oldValue = new String("old");
        Transit<String> transit = new Transit<>(oldValue, null);
        RuntimeKey<String> runtimeKey = RuntimeKey.valueOf(transit, new TypeKey<>(String.class), null, false);

        SplitResult result = splitEvent.call(runtimeKey, indexes.eventField());

        assertSame(transit, result.event());
        assertTrue(result.evaluables().isEmpty());
    }
}
