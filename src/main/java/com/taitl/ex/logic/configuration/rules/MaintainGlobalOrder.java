package com.taitl.ex.logic.configuration.rules;

import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.evaluables.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * Maintains global order of event handlers as declared,
 * to ensure they are called in same order.
 * This is not critical for validation of boolean rules,
 * but is important for the rules with side effects.
 */
public class MaintainGlobalOrder
{
    AtomicLong maxCount = new AtomicLong(0);

    public <T> OrderlyEv<T> globallyOrdered(Ev<T> ev)
    {
        return new OrderlyEv<>(ev, maxCount.getAndIncrement());
    }

    public <T> void sort(List<OrderlyEv<?>> handlers)
    {
        handlers.sort(Comparator.comparingLong(OrderlyEv::ordinal));
    }
}
