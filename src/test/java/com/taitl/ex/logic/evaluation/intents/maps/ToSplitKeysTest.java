package com.taitl.ex.logic.evaluation.intents.maps;

import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ToSplitKeysTest
{
    @Test
    @DisplayName("Group by event type rejects runtime key without event")
    void groupByEventTypeRejectsRuntimeKeyWithoutEvent()
    {
        ToSplitKeys toSplitKeys = new ToSplitKeys();
        Set<RuntimeKey<String>> runtimeKeys = new LinkedHashSet<>();
        runtimeKeys.add(RuntimeKey.valueOf(String.class, "String", "value", false));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> toSplitKeys.groupByEventType(runtimeKeys));

        assertEquals("Argument 'event' must not be null", error.getMessage());
    }
}
