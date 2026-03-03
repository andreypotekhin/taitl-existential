package com.taitl.existential;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.io.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Entry point to the Existential library.
 * Provides access to configuration, transactions, and event dispatch.
 * Documentation: https://github.com/andreypotekhin/taitl-existential
 *
 * @author Andrey Potekhin
 */
public final class Existential implements Closeable
{
    private ExistentialInit init;
    private ExistentialAccess access;
    private ExistentialConfigs configs;
    private ExistentialTransactions transactions;
    private ExistentialEvents events;
    private ExistentialFlags flags;

    private boolean configured = false;
    private boolean closed = false;

    public Existential()
    {
        init = Creator.create(ExistentialInit.class, new Class[] { Existential.class }, this);
        access = Creator.create(ExistentialAccess.class, new Class[] { Existential.class }, this);
        configs = Creator.create(ExistentialConfigs.class, new Class[] { Existential.class }, this);
        transactions = Creator.create(ExistentialTransactions.class, new Class[] { Existential.class }, this);
        events = Creator.create(ExistentialEvents.class, new Class[] { Existential.class }, this);
        flags = Creator.create(ExistentialFlags.class, new Class[] { Existential.class }, this);
        init.startup();
    }

    /**
     * Starts configuration for the specified business operation.
     *
     * @param op operation name, for example "/app/orders/update"
     * @return builder used to configure contexts and rules
     */
    public ConfigBuilder configure(String op)
    {
        return configs.getBuilder(op);
    }

    /**
     * Begins a transaction for the specified operation.
     *
     * @param op operation name
     * @return transaction identifier
     * @throws ExistentialException when transaction start fails
     */
    public Tr begin(String op) throws ExistentialException
    {
        return transactions.begin(op);
    }

    /**
     * Begins a transaction for the specified operation using a custom Transaction instance.
     *
     * @param op operation name
     * @param custom transaction instance to use
     * @return transaction identifier
     * @throws ExistentialException when transaction start fails
     */
    public Tr begin(String op, Transaction custom) throws ExistentialException
    {
        return transactions.begin(op, custom);
    }

    /**
     * Commits an existential transaction.
     * Validates rules configured for the transaction's business operation.
     * After commit, tranID becomes invalid.
     *
     * @param tranID transaction id
     * @throws ExistentialException when validation or commit fails
     */
    public void commit(String tranID) throws ExistentialException
    {
        transactions.commit(tranID);
    }

    /**
     * Commits an existential transaction.
     * Validates rules configured for the transaction's business operation.
     * After commit, tranID becomes invalid.
     *
     * @param tr transaction object
     * @throws ExistentialException when validation or commit fails
     */
    public void commit(Tr tr) throws ExistentialException
    {
        transactions.commit(tr);
    }

    /**
     * Creates a checkpoint in the transaction lifecycle.
     *
     * @param tranID transaction identifier
     * @throws ExistentialException when checkpoint fails
     */
    public void checkpoint(String tranID) throws ExistentialException
    {
        transactions.checkpoint(tranID);
    }

    /**
     * Creates a checkpoint in the transaction lifecycle.
     *
     * @param tr transaction object
     * @throws ExistentialException when checkpoint fails
     */
    public void checkpoint(Tr tr) throws ExistentialException
    {
        transactions.checkpoint(tr);
    }

    /**
     * Rolls back an existential transaction.
     * Rule validation is not performed.
     * After rollback, tranID becomes invalid.
     *
     * @param tranID transaction id
     * @throws ExistentialException when rollback fails
     */
    public void rollback(String tranID) throws ExistentialException
    {
        transactions.rollback(tranID);
    }

    /**
     * Rolls back an existential transaction.
     * Rule validation is not performed.
     * After rollback, tranID becomes invalid.
     *
     * @param tr transaction object
     * @throws ExistentialException when rollback fails
     */
    public void rollback(Tr tr) throws ExistentialException
    {
        transactions.rollback(tr);
    }

    /* Event methods */

    /**
     * Emits an event for a single entity value and an explicit type key.
     *
     * @param event event value
     * @param t entity value
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void event(Event<T> event, T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(event, "event", t, "t", type, "type", tranID, "tranID");
        events.event(event, t, type, tranID);
    }

    /**
     * Emits an event based on before- and after- entity states.
     *
     * @param event event value, holding before- and after- states
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void event(BiEvent<T> event, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(event, "event", type, "type", tranID, "tranID");
        events.event(event, type, tranID);
    }

    /* Event methods: convenience / shortcut methods */

    /**
     * Emits Create<T> event.
     *
     * @param t entity value
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void create(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.create(t, type, tranID);
    }

    /**
     * Variant of create() without type parameter. Only suitable for non-generic entity types.
     *
     * @param t entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void create(T t, String tranID) throws ExistentialException
    {
        events.create(t, tranID);
    }

    /**
     * Emits Delete<T> event.
     *
     * @param t entity value
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void delete(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.delete(t, type, tranID);
    }

    /**
     * Variant of delete() without type parameter. Only suitable for non-generic entity types.
     *
     * @param t entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void delete(T t, String tranID) throws ExistentialException
    {
        events.delete(t, tranID);
    }

    /**
     * Emits Update<T> event.
     *
     * @param t entity value
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void update(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.update(t, type, tranID);
    }

    /**
     * Variant of update() without type parameter. Only suitable for non-generic entity types.
     *
     * @param t entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void update(T t, String tranID) throws ExistentialException
    {
        events.update(t, tranID);
    }

    /**
     * Emits Mutate<T> event based on before- and after- entity states.
     *
     * @param t0 previous entity value
     * @param t1 new entity value
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void mutate(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.mutate(t0, t1, type, tranID);
    }

    /**
     * Variant of mutate() without type parameter. Only suitable for non-generic entity types.
     *
     * @param t0 previous entity value
     * @param t1 new entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void mutate(T t0, T t1, String tranID) throws ExistentialException
    {
        events.mutate(t0, t1, tranID);
    }

    /**
     * Emits Port<T> event based on before- and after- entity states,
     * where one of these states may be null.
     *
     * @param t0 previous entity value (if null, indicates entity creation)
     * @param t1 new entity value (if null, indicates entity deletion)
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void port(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.port(t0, t1, type, tranID);
    }

    /**
     * Variant of port() without type parameter. Only suitable for non-generic entity types.
     *
     * @param t0 previous entity value
     * @param t1 new entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void port(T t0, T t1, String tranID) throws ExistentialException
    {
        events.port(t0, t1, tranID);
    }

    /* Access event methods */

    /**
     * Emits an event based on before and after entity states.
     * where one of these states may be null.
     *
     * @param entity entity being read
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void read(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
        access.read(entity, type, tranID);
    }

    /**
     * Emits a read event for the entity.
     *
     * @param entity entity being read
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void read(T entity, String tranID) throws ExistentialException
    {
        access.read(entity, tranID);
    }

    /**
     * Emits a write event for the entity using an explicit type key.
     *
     * @param entity entity being written
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void write(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
        access.write(entity, type, tranID);
    }

    /**
     * Emits a write event for the entity.
     *
     * @param entity entity being written
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void write(T entity, String tranID) throws ExistentialException
    {
        access.write(entity, tranID);
    }

    /* Flag methods */

    /**
     * Returns the current value of a library behavior flag.
     *
     * @param flag flag identifier from Flags
     * @return current flag value
     */
    public boolean get(int flag)
    {
        return flags.get(flag);
    }

    /**
     * Enables the specified library behavior flag.
     *
     * @param flag flag identifier from Flags
     */
    public void on(int flag)
    {
        flags.on(flag);
    }

    /**
     * Disables the specified library behavior flag.
     *
     * @param flag flag identifier from Flags
     */
    public void off(int flag)
    {
        flags.off(flag);
    }

    /**
     * Flips the specified library behavior flag.
     *
     * @param flag flag identifier from Flags
     * @return updated flag value
     */
    public boolean toggle(int flag)
    {
        return flags.toggle(flag);
    }

    /**
     * Closes the library instance and releases resources.
     */
    public void close()
    {
        if (!closed)
        {
            transactions.close();
            events.close();
            flags.close();
            configs.close();
            access.close();
            init.close();
            closed = true;
        }
    }

    /**
     * Sets the configured status for the library.
     *
     * @param b configured flag value
     */
    public void configured(boolean b)
    {
        configured = b;
    }

    /**
     * Reports whether the library has completed configuration.
     *
     * @return true when configured
     */
    public boolean configured()
    {
        return configured;
    }

    /**
     * Returns the configuration subsystem for inspection or advanced usage.
     *
     * @return ExistentialConfigs instance
     */
    public ExistentialConfigs configs()
    {
        return configs;
    }

    /**
     * Returns the transaction subsystem for inspection or advanced usage.
     *
     * @return ExistentialTransactions instance
     */
    public ExistentialTransactions transactions()
    {
        return transactions;
    }

    /**
     * Returns the event subsystem for inspection or advanced usage.
     *
     * @return ExistentialEvents instance
     */
    public ExistentialEvents events()
    {
        return events;
    }
}
