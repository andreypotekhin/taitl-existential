package com.taitl.ex.logic.managed_indexes.actions;

import com.taitl.ex.logic.managed_indexes.data.*;
import java.util.*;

/** Applies an extractor-validated batch without retaining the repeated report stream. */
public class FoldIndexChanges
{
    public void call(List<ManagedChange> changes)
    {
        for (ManagedChange change : changes)
        {
            change.apply();
        }
    }
}
