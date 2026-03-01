package com.taitl.existential.events.types;

import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;

/**
 * Marker interface for events about an entity.
 * Serves as a base type for more specific events, such as Create<T>, Update<T>, and Delete<T>.
 *
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Delete
 * @see Update
 * @see CU
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Transit
 */
public interface Event<T>
{
}
