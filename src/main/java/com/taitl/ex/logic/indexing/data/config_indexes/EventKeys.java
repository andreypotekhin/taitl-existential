package com.taitl.ex.logic.indexing.data.config_indexes;

import java.util.*;
import com.taitl.ex.logic.events.logic.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Stores the event keys for which a rule is present in a Transaction or its Contexts.
 * Maps event key to a boolean (does a rule exist for this type?)
 * Example: "Create<Doc<JSON>>", "Update<Doc<HTML>>": rules (expressions, event handlers)
 * exist for these events, corresponding non-generic versions ("Create<Doc>", "Update<Doc>",
 * "Create", "Update"), or their elementary versions ("Create", "Update", "Change").
 *
 * Different contexts may have rules to different types of events.
 * To speed up the answer to question 'which events should be emitted by EventSplitter
 * for this context?', the set of relevant events (from the context and from all its
 * parents and matching contexts) is created at transaction start and stored in the
 * Transaction object. This allows to avoid having to gather such info for each individual
 * event from each applicable context.
 *
 * @see Tr
 * @see EventSplitter
 */
public class EventKeys
{
    Set<String> eventKeys = new LinkedHashSet<>();

    void add(EventKey eventKey)
    {
        sane(eventKey, "eventKey");
        if (!eventKeys.contains(eventKey.toString()))
        {
            eventKeys.add(eventKey.toString());
            // TODO: add generic and elementary versions of the event key,
            // e.g. for "ReadAndLock<Doc<JSON>>" also add "ReadAndLock<Doc>", "ReadAndLock",
            // "Read<Doc<JSON>>", "Read<Doc>", "Change"
        }
    }

    boolean contains(EventKey eventKey)
    {
        sane(eventKey, "eventKey");
        return eventKeys.contains(eventKey.toString());
    }
}
