package com.taitl.ex.logic.managed_indexes.actions;

import com.taitl.ex.logic.managed_indexes.data.*;
import java.util.*;

/** Flushes folded affected groups at a boundary, independently of user effect declarations. */
public class PrepareManagedIndexes
{
    public void call(Collection<ManagedBinding> bindings)
    {
        for (ManagedBinding binding : bindings)
        {
            for (ManagedSide<Object, Object> side : binding.sides())
            {
                side.flush();
            }
        }
    }
}
