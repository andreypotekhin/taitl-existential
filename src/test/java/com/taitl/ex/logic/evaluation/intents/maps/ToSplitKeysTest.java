package com.taitl.ex.logic.evaluation.intents.maps;

import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ToSplitKeysTest
{
    ToSplitKeys toSplitKeys;
    Set<RuntimeKey<String>> runtimeKeys;

    @BeforeEach
    void setup()
    {
        toSplitKeys = new ToSplitKeys();
        runtimeKeys = new LinkedHashSet<>();
        runtimeKeys.add(RuntimeKey.valueOf(String.class, "String", "value", false));
    }

    @Nested
    class GroupByEventType
    {
        @Test
        @DisplayName("Rejects runtime key without event")
        void rejectsMissingEvent()
        {
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> toSplitKeys.groupByEventType(runtimeKeys));

            assertEquals("Argument 'event' must not be null", error.getMessage());
        }
    }
}
