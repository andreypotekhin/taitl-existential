package com.taitl.ex.logic.indexing.data.runtime_indexes;

import java.util.*;
import com.taitl.ex.logic.events.logic.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

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

    void add(TypeKey eventKey)
    {
        sane(eventKey, "eventKey");
        String key = eventKey.toString();
        if (eventKeys.add(key))
        {
            // TODO: also add generic and elementary versions of the event key type:
            // e.g. for "ReadAndLock<Doc<JSON>>" also add "ReadAndLock<Doc<?>>", "ReadAndLock<Doc>"
            // "Read<Doc<JSON>>", "Read<Doc<?>>", "Read<Doc>"
        }
    }

    boolean contains(TypeKey eventKey)
    {
        sane(eventKey, "eventKey");
        return eventKeys.contains(eventKey.toString());
    }
}
