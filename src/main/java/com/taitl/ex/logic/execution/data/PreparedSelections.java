package com.taitl.ex.logic.execution.data;

import com.taitl.ex.common.helper.collections.*;

/**
 * One EventField generation's compiled inventory and bounded selection cache.
 * Compact dispatch signatures and legacy expanded-key strings share the same entry budget;
 * empty selections count toward that budget as well as successful matches.
 *
 * The inventory identity defines the generation. EventField replaces this holder when its
 * rule source changes, preventing selections from an older inventory from being reused.
 * Entries contain reusable rule selections, never reported payloads or transaction memo values.
 */
public class PreparedSelections
{
    public static final int CAPACITY = 1024;
    public RuleInventory inventory;
    public BoundedCache<Object, RuleSelection> entries = new BoundedCache<>(CAPACITY);

    public PreparedSelections(RuleInventory inventory)
    {
        this.inventory = inventory;
    }
}
