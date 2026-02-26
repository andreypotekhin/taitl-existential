package com.taitl.ex.logic.events.logic;

import java.util.*;

import com.taitl.ex.logic.evaluation.logic.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.types.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class EventSplitterTest
{
    private static final class TestEventSplitter extends EventSplitter
    {
        private <T> Set<Event<T>> splitTransitPublic(Transit<T> transit, Set<Event<T>> set)
        {
            return splitTransit(transit, set);
        }
    }

    @Test
    void splitTransitUsesUpdatedEntityForChange()
    {
        EventSplitter splitter = new EventSplitter();
        String oldValue = new String("old");
        String newValue = new String("new");

        Set<Event<String>> events = splitter.split(new Transit<>(oldValue, newValue));

        Change<String> change = events.stream()
                .filter(event -> event instanceof Change<?>)
                .map(event -> (Change<String>) event)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Change event not found"));

        assertSame(newValue, change.t);
    }

    @Test
    void splitTransitRejectsNullTransitWithEventMessage()
    {
        TestEventSplitter splitter = new TestEventSplitter();

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> splitter.splitTransitPublic(null, new LinkedHashSet<>()));

        assertEquals("Argument 'event' should not be null", error.getMessage());
    }
}
