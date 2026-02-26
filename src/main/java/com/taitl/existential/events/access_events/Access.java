package com.taitl.existential.events.access_events;

import com.taitl.existential.events.types.*;

/**
 * Indicates that entity was accessed in the course of current transaction.
 * 
 * Example: Access<Account> is sent when Account entity has been accessed.
 *
 * Since this event may be cumbersome to use (e.g. you need to send it
 * when an entity has been accessed in any way), it is reserved for advanced
 * cases where you need tight control. Consider using Read<T>, ReadAndLock<T>
 * Write<T> which may have fewer points to track.
 *
 * Database analog: None. Event is intended to track runtime access
 * to entities in memory. To track database/storage accesses, use
 * Read<T>/ReadAndLock<T>/Write<T>.
 * 
 * @param <T>
 *            Class of application entity
 */
public class Access<T> extends EntityEvent<T>
{
    public Access(T t)
    {
        super(t);
    }
}
