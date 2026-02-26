package com.taitl.ex.logic.indexing.data;

import com.taitl.ex.logic.evaluation.logic.*;
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
public class EncounteredUniqueEvents
{
    protected Set<RuntimeKey<?>> runtimeKeys = new LinkedHashSet<>();

    public <T> void add(RuntimeKey<T> key)
    {
        sane(key, "key");
        runtimeKeys.add(key);
    }

    public <T> boolean contains(RuntimeKey<T> key)
    {
        sane(key, "key");
        return runtimeKeys.contains(key);
    }

    public <T> Stream<RuntimeKey<?>> stream()
    {
        return runtimeKeys.stream();
    }
}
