package com.taitl.ex.logic.unused.indexes;

import com.taitl.ex.logic.evaluation.logic.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Stores the event keys (event+type) for which a rule has been configured in a Transaction or its Contexts.
 * Maps event key to a boolean (does a rule exist for this type?)
 * Example: "Create<Doc<JSON>>", "Update<Doc<HTML>>": rules (expressions, event handlers)
 * exist for these events , or for their non-generic versions ("Create<Doc<?>>", "Update<Doc<?>>",
 * "Create<Doc>", "Update<Doc>").
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
@Deprecated
public class ConfiguredEventKeys
{
    Set<String> eventKeys = new LinkedHashSet<>();

    void add(TypeKey eventKey)
    {
        sane(eventKey, "eventKey");
        if (!eventKeys.contains(eventKey.toString()))
        {
            eventKeys.add(eventKey.toString());
        }
    }

    boolean contains(TypeKey eventKey)
    {
        sane(eventKey, "eventKey");
        return eventKeys.contains(eventKey.toString());
    }

    public void clear()
    {
        eventKeys.clear();
    }
}
