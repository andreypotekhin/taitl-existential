package com.taitl.ex.logic.configuration.indexes.data;

import com.taitl.ex.logic.configuration.rules.*;
import com.taitl.existential.evaluables.*;

/**
 * An event handler (rule) along with its global ordinal number
 * in the sequence of rules configured for a business operation, plus whether it came from an
 * effect declaration.
 * Used in conjunction with MaintainGlobalOrder to enforce
 * the calling order on event handlers to follow their
 * declaration order.
 *
 * @see MaintainGlobalOrder
 */
public class OrderlyEv<T>
{
    public Ev<T> ev;
    public long ordinal;
    public boolean effect;
    public com.taitl.ex.logic.execution.data.RuleDeclaration declaration;
    public com.taitl.ex.logic.diagnostics.data.RuleOrigin origin;

    public OrderlyEv(Ev<T> ev, long ordinal)
    {
        this(ev, ordinal, false);
    }

    public OrderlyEv(Ev<T> ev, long ordinal, boolean effect)
    {
        this.ev = ev;
        this.ordinal = ordinal;
        this.effect = effect;
    }

    public static <T> OrderlyEv<T> of(Ev<T> ev, int order)
    {
        return new OrderlyEv<>(ev, order);
    }

    public Ev<T> ev()
    {
        return ev;
    }

    public long ordinal()
    {
        return ordinal;
    }
}
