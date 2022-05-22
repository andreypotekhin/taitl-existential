package com.taitl.existential.event.types;

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
 * Indicates something that has happened to an application entity.
 *
 * Serves as base class to more specific events ({@code Create<T>, Update<T>, Delete<T>,} etc.)
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
