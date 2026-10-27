package com.taitl.ex.logic.managed_indexes.data;

import com.taitl.existential.*;
import com.taitl.existential.indexes.*;
import java.util.*;
import static com.taitl.ex.common.helper.Args.*;

/** Stores declared index tokens and inheritable options until the owning scope is bound. */
public class IndexConfiguration
{
    private final List<IndexDefinition<?>> definitions = new ArrayList<>();
    private IndexingPolicy policy = IndexingPolicy.inherit();
    private boolean frozen;

    public void add(IndexDefinition<?> definition)
    {
        mutable();
        sane(definition, "definition");
        if (!definitions.contains(definition))
        {
            definitions.add(definition);
        }
    }

    public List<IndexDefinition<?>> definitions()
    {
        return Collections.unmodifiableList(definitions);
    }

    public IndexingPolicy policy()
    {
        return policy;
    }

    public void policy(IndexingPolicy value)
    {
        mutable();
        sane(value, "policy");
        policy = value;
    }

    public void freeze()
    {
        frozen = true;
    }

    private void mutable()
    {
        if (frozen)
        {
            throw new IllegalStateException(
                    "Managed indexing configuration is already bound; configure before begin().");
        }
    }
}
