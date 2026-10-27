package com.taitl.ex.logic.managed_indexes.actions;

import com.taitl.ex.logic.managed_indexes.data.*;
import java.util.*;

/** Clears owners in place so retained facade aliases cannot retain former entity groups. */
public class DisposeManagedIndexes
{
    public void call(Collection<ManagedBinding> bindings)
    {
        for (ManagedBinding binding : bindings)
        {
            binding.close();
        }
    }
}
