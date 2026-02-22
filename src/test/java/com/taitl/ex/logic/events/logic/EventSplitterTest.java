package com.taitl.ex.logic.events.logic;

import java.util.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.types.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class EventSplitterTest
{
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
}
