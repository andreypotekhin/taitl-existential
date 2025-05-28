package com.taitl.existential.events.types;

import com.taitl.existential.constants.Strings;
import com.taitl.existential.events.Create;
import com.taitl.existential.events.Delete;
import com.taitl.existential.events.Mutate;
import com.taitl.existential.events.Read;
import com.taitl.existential.events.ReadAndLock;
import com.taitl.existential.events.Transit;
import com.taitl.existential.events.Update;
import com.taitl.existential.events.Upsert;
import com.taitl.existential.events.Write;

/**
 * Indicates the fact of reading, accessing or changing an application entity.
 * Serves as base class to more specific events ({@code Create<T>, Update<T>,} etc.)
 * <p>
 * Example: {@code EntityEvent<Account>} is emitted when Account entity was accessed or changed in the course of current transaction.
 * <p>
 * Database analogs: SELECT, INSERT, UPDATE or DELETE
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
public class EntityEvent<T> implements Event<T>
{
    public T t;

    public EntityEvent(T t)
    {
        if (t == null)
        {
            throw new IllegalArgumentException(Strings.ARG_T);
        }
        this.t = t;
    }
}
