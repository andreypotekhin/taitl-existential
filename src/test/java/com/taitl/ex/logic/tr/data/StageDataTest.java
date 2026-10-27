package com.taitl.ex.logic.tr.data;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class StageDataTest
{
    @Test
    void replacingPreparedIndexesInvalidatesIntentOverlay()
    {
        ConfigurationIndexes base = new ConfigurationIndexes();
        ConfigurationIndexes first = new ConfigurationIndexes();
        ConfigurationIndexes second = new ConfigurationIndexes();
        base.doneIndexing();
        first.doneIndexing();
        second.doneIndexing();
        StageData data = new StageData();
        try
        {
            data.preparedIndexes(first);
            EventField original = data.intentField(base.intentField());
            data.preparedIndexes(second);
            assertNotSame(original, data.intentField(base.intentField()));
            assertSame(data.intentField(base.intentField()), data.intentField(base.intentField()));
        }
        finally
        {
            data.close();
            base.close();
        }
    }
}
