package com.taitl.existential.mutations;

import com.taitl.ex.common.helper.*;

/**
 * Represents an object mutation that, unlike Transition<T>, allows either 'from' or 'to' state to be null (but not both).
 *
 * t0 represents object data at the start of transaction (object initial state). t1 represents object data at the end of
 * transaction (object final state).
 *
 * If t0 is null, this indicates a newly created object. If t1 is null, this indicates a deleted object (the object
 * about to be deleted as part of this transaction).
 *
 * @param <T>
 *            Type of mutating object
 *
 * @see Transition
 */
public final class Porting<T>
{
    public T t0;
    public T t1;

    public Porting(T t0, T t1)
    {
        PairArgs.requireNotBothNull(t0, t1, "Arguments 't0' and 't1' should not be both null");
        this.t0 = t0;
        this.t1 = t1;
    }
}
