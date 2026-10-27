package com.taitl.ex.logic.managed_indexes.actions;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.common.annotations.*;
import com.taitl.ex.logic.managed_indexes.*;
import com.taitl.ex.logic.managed_indexes.data.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.transactions.*;
import java.util.*;

/** Resolves scope precedence once and constructs independent holders before begin callbacks run. */
public class BindManagedIndexes
{
    @Logic
    private IndexingPolicyLogic policies = Creator.create(IndexingPolicyLogic.class);

    public Map<IndexDefinition<?>, ManagedBinding> call(Tr tr, Existential ex, ManagedAccess access)
    {
        IndexingPolicy policy = policies.resolve(tr, ex.indexingPolicy());
        Set<Context> contexts = Collections.newSetFromMap(new IdentityHashMap<>());
        List<IndexDefinition<?>> definitions = new ArrayList<>();
        for (Transaction transaction : tr.transactions())
        {
            Context context = transaction.context();
            if (context != null && contexts.add(context))
            {
                definitions.addAll(context.indexes());
                context.freezeIndexes();
            }
        }
        for (Transaction transaction : tr.transactions())
        {
            definitions.addAll(transaction.indexes());
            transaction.freezeIndexes();
        }
        Map<IndexDefinition<?>, ManagedBinding> result = new IdentityHashMap<>();
        Map<String, IndexDefinition<?>> names = new HashMap<>();
        for (IndexDefinition<?> definition : definitions)
        {
            IndexDefinition<?> previous = names.putIfAbsent(definition.name(), definition);
            if (previous != null && previous != definition)
            {
                throw new IllegalArgumentException("Managed index name '" + definition.name()
                        + "' has conflicting definitions for operation '" + tr.op
                        + "'. Use unique names or reuse one definition. See /Troubleshooting.md#managed-index-maintenance");
            }
            if (!result.containsKey(definition))
            {
                result.put(definition, Creator.create(ManagedBinding.class,
                        new Class<?>[] { IndexDefinition.class, IndexingPolicy.class, ManagedAccess.class },
                        definition, definition.policy().over(policy), access));
            }
        }
        return result;
    }
}
