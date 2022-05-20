package com.taitl.existential.event.base;

import com.taitl.existential.constants.Strings;
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
 * Indicates loading, access or change of an application entity. Serves as base class to more specific events
 * ({@code Create<T>, Update<T>,} etc.)
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
