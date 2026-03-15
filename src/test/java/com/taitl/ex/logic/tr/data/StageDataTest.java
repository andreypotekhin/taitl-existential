package com.taitl.ex.logic.tr.data;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class StageDataTest
{
    @Test
    @DisplayName("Caches overlay per prepared indexes and resets it when indexes change")
    void cachesOverlayAndResetsItOnPreparedIndexesChange()
    {
        StageData data = new StageData();
        EventField base = baseEventField();

        assertSame(base, data.eventField(base));

        data.preparedIndexes(indexes());
        EventField first = data.eventField(base);
        assertNotSame(base, first);
        assertSame(first, data.eventField(base));

        data.preparedIndexes(indexes());
        EventField second = data.eventField(base);
        assertNotSame(first, second);
    }

    @Test
    @DisplayName("Returns base field after close")
    void returnsBaseFieldAfterClose()
    {
        StageData data = new StageData();
        EventField base = baseEventField();

        data.preparedIndexes(indexes());
        assertNotSame(base, data.eventField(base));

        data.close();

        assertSame(base, data.eventField(base));
    }

    protected ConfigurationIndexes indexes()
    {
        ConfigurationIndexes indexes = new ConfigurationIndexes();
        indexes.doneIndexing();
        return indexes;
    }

    protected EventField baseEventField()
    {
        return indexes().eventField();
    }
}
