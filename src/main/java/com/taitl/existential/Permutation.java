package com.taitl.existential;

import static com.taitl.existential.constants.Strings.ARG_T0_T1;

/**
 * Represents an object mutation that, unlike Mutation<T>, allows either 'from' or 'to' state to be null (but not both).
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
 * @author Andrey Potekhin
 * @see Mutation
 */
public final class Permutation<T>
{
    public T t0;
    public T t1;

    Permutation(T t0, T t1)
    {
        if (t0 == null && t1 == null)
        {
            throw new IllegalArgumentException(ARG_T0_T1);
        }
        this.t0 = t0;
        this.t1 = t1;
    }
}
