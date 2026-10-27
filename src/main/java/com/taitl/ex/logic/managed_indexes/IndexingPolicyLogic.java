package com.taitl.ex.logic.managed_indexes;

import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.transactions.*;
import java.util.*;

/** Resolves independent inherited options in context order, followed by transaction overrides. */
public class IndexingPolicyLogic
{
    public IndexingPolicy resolve(Tr tr, IndexingPolicy defaults)
    {
        IndexingPolicy policy = defaults;
        Set<Context> contexts = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Transaction transaction : tr.transactions())
        {
            Context context = transaction.context();
            if (context != null && contexts.add(context))
            {
                policy = context.indexingPolicy().over(policy);
            }
        }
        for (Transaction transaction : tr.transactions())
        {
            policy = transaction.indexingPolicy().over(policy);
        }
        return policy;
    }
}
