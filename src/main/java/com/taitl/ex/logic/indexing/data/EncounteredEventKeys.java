package com.taitl.ex.logic.indexing.data;

import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;
import java.util.stream.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Stores the event keys (event+type) encountered in the course of transaction.
 * Maps event key to a boolean (was an event of this key encountered?)
 * Example: for an event "Create<Doc<JSON>>", stores event key "Create<Doc<JSON>>"
 * and the non-generic versions ("Create<Doc<?>>", "Create<Doc>").
 *
 * @see Tr
 * @see EventSplitter
 */
public class EncounteredEventKeys
{
    Set<String> eventKeys = new LinkedHashSet<>();

    public <T> void add(EventKey<T> eventKey)
    {
        sane(eventKey, "eventKey");
        eventKeys.add(eventKey.toString());
    }

    public <T> boolean contains(EventKey<T> eventKey)
    {
        sane(eventKey, "eventKey");
        return eventKeys.contains(eventKey.toString());
    }

    public Stream<String> stream()
    {
        return eventKeys.stream();
    }
}
