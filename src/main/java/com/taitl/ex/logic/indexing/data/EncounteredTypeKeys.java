package com.taitl.ex.logic.indexing.data;

import com.taitl.ex.logic.evaluation.logic.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;
import java.util.stream.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * The type keys encountered in the course of transaction.
 * Maps type key to a boolean (was an event of this key encountered?)
 * Example: for an event "Create<Doc<JSON>>", stores type key "Doc<JSON>"
 * and the non-generic versions ("Doc<?>", "Doc").
 *
 * @see Tr
 * @see EventSplitter
 */
public class EncounteredTypeKeys
{
    Set<String> typeKeys = new LinkedHashSet<>();

    public <T> void add(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        String key = typeKey.toString();
        typeKeys.add(key);
    }

    public <T> boolean contains(TypeKey<T> eventKey)
    {
        sane(eventKey, "eventKey");
        return typeKeys.contains(eventKey.toString());
    }

    public <T> Stream<String> stream()
    {
        return typeKeys.stream();
    }
}
