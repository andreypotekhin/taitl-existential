package com.taitl.existential.mutations;

import com.taitl.ex.common.helper.*;

/**
 * Represents an object mutation that, unlike Transition<T>, does not allow either 'from' nor 'to' state to be null.
 *
 * t0 represents object data at the start of transaction (object initial state). t1 represents object data at the end of
 * transaction (object final state). Neither t0 nor t1 may be null.
 *
 * @param <T>
 *            Type of mutating object
 *
 * @see Porting
 */
public final class Mutation<T>
{
    public T t0;
    public T t1;

    public Mutation(T t0, T t1)
    {
        PairArgs.requireBothNonNull(t0, t1, "Argument 't0' should not be null", "Argument 't1' should not be null");
        this.t0 = t0;
        this.t1 = t1;
    }
}
