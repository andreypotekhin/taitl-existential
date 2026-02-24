package com.taitl.existential;

import java.io.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;

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
    private ExistentialTransactions transactions;
    private ExistentialEvents events;
    private ExistentialFlags flags;
    private ExistentialConfigs configs;

    private boolean configured = false;
    private boolean closed = false;

    public Existential()
    {
        init = Creator.create(ExistentialInit.class, new Class[] { Existential.class }, this);
        access = Creator.create(ExistentialAccess.class, new Class[] { Existential.class }, this);
        transactions = Creator.create(ExistentialTransactions.class, new Class[] { Existential.class }, this);
        events = Creator.create(ExistentialEvents.class, new Class[] { Existential.class }, this);
        flags = Creator.create(ExistentialFlags.class, new Class[] { Existential.class }, this);
        configs = Creator.create(ExistentialConfigs.class, new Class[] { Existential.class }, this);
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
    public String begin(String op) throws ExistentialException
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
    public String begin(String op, Transaction custom) throws ExistentialException
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
     * Emits an event based on a pair of entity versions and an explicit type key.
     *
     * @param t0 previous entity value
     * @param t1 new entity value
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void event(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.event(t0, t1, type, tranID);
    }

    /**
     * Emits an event for a single entity value and an explicit type key.
     *
     * @param t entity value
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void event(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.event(t, type, tranID);
    }

    /**
     * Emits an event based on a pair of entity versions.
     *
     * @param t0 previous entity value
     * @param t1 new entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void event(T t0, T t1, String tranID) throws ExistentialException
    {
        events.event(t0, t1, tranID);
    }

    /**
     * Emits an event for a single entity value.
     *
     * @param t entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public <T> void event(T t, String tranID) throws ExistentialException
    {
        events.event(t, tranID);
    }

    /**
     * Emits a read event for the entity using an explicit type key.
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
}
