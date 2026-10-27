package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.logic.transactions.data.*;
import java.util.*;

/** Formats bounded, deterministic owner diagnostics from existing shutdown accounting. */
public class DescribeOutstandingResources
{
    public String call(TrRegistry registry, int inFlight)
    {
        List<String> details = registry.descriptions(8);
        return " Known resources: activeTransactions=" + registry.size() + ", inFlight=" + inFlight
                + ", transactions=" + details + ". At most 8 transaction descriptions are shown.";
    }
}
