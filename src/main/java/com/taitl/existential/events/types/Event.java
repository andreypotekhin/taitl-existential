package com.taitl.existential.events.types;

import com.taitl.existential.events.*;

/**
 * Marker interface for any event about an entity.
 * Serves as base class to more specific events, such as {@code Create<T>, Update<T>, Delete<T>,} etc.
 *
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
