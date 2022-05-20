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
 * Indicates something that has happened to an application entity. Serves as base class to more specific events
 * ({@code Create<T>, Update<T>,} etc.)
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
public interface Event<T>
{
}
