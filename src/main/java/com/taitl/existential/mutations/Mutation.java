package com.taitl.existential.mutations;

import static com.taitl.existential.constants.Strings.ARG_T0;
import static com.taitl.existential.constants.Strings.ARG_T1;

/**
 * Represents an object mutation that, unlike Permutation<T>, does not allow either 'from' nor 'to' state to be null.
 * 
 * t0 represents object data at the start of transaction (object initial state). t1 represents object data at the end of
 * transaction (object final state).
 *
 * @param <T>
 *            Type of mutating object
 * 
 * @author Andrey Potekhin
 * @see Transition
 */
public final class Mutation<T>
{
    public T t0;
    public T t1;

    public Mutation(T t0, T t1)
    {
        if (t0 == null)
        {
            throw new IllegalArgumentException(ARG_T0);
        }
        if (t1 == null)
        {
            throw new IllegalArgumentException(ARG_T1);
        }
        this.t0 = t0;
        this.t1 = t1;
    }
}
