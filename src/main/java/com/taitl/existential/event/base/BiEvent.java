package com.taitl.existential.event.base;

import com.taitl.existential.event.type.Create;
import com.taitl.existential.event.type.Delete;
import com.taitl.existential.event.type.Mutate;
import com.taitl.existential.event.type.Read;
import com.taitl.existential.event.type.ReadAndLock;
import com.taitl.existential.event.type.Transit;
import com.taitl.existential.event.type.Update;
import com.taitl.existential.event.type.Upsert;
import com.taitl.existential.event.type.Write;

/**
 * Indicates some change that happened to an application entity. Serves as base class to mutation events ({@code Mutate<T>,
 * Permutate<T>})
 * <p>
 * Unlike {@code Event<T>} class, provides initial and final entity states for the transaction.
 * <p>
 * Initial state (t0): entity state in the beginning of transaction<br> 
 * Final state (t1): entity state in the end of transaction<br>
 * <p>
 * Example: BiEvent<Account> is raised when Account entity was changed in the course of current transaction.
 * <p> 
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Delete
 * @see Update
 * @see Upsert
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Transit
 */
public class BiEvent<T> implements Event<T>
{
    public T t0;
    public T t1;

    public BiEvent(T t0, T t1)
    {
        this.t0 = t0;
        this.t1 = t1;
    }
}
