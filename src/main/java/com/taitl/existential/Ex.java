package com.taitl.existential;

import com.taitl.ex.core.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Static facade for the Existential library.
 * Holds a single {@link Existential} instance and delegates all calls to it.
 * This convenience implies a single {@link Existential} instance per JVM. This is
 * fine for most applications, but it can be limiting in library code.
 *
 * Usage: Ex.configure("/api/resource/create").context()...
 * Documentation: https://github.com/andreypotekhin/taitl-existential
 *
 * @author Andrey Potekhin
 * @see Existential
 */
public final class Ex
{
    /**
     * Instance holder for lazy initialization.
     */
    private static class InstanceHolder
    {
        static Existential instance = new Existential();
    }

    private static Existential instance()
    {
        return InstanceHolder.instance;
    }

    /**
     * Instance setter for testing purposes.
     * Note: this method is not intended for production use.
     *
     * @param instance instance to install for testing
     * @return previous instance
     */
    public static Existential instance(Existential instance)
    {
        sane(instance, "instance");
        Existential prevInstance = InstanceHolder.instance;
        InstanceHolder.instance = instance;
        return prevInstance;
    }

    /**
     * Starts configuration for the specified business operation.
     *
     * @param op operation name, for example "/app/orders/update"
     * @return builder used to configure contexts and rules
     */
    public static ConfigBuilder configure(String op)
    {
        return instance().configure(op);
    }

    /**
     * Begins a transaction for the specified operation.
     *
     * @param op operation name
     * @return transaction object
     * @throws ExistentialException when transaction start fails
     */
    public static Tr begin(String op) throws ExistentialException
    {
        return instance().begin(op);
    }

    /**
     * Begins a transaction for the specified operation using a custom Transaction instance.
     *
     * @param op operation name
     * @param custom transaction instance to use
     * @return transaction object
     * @throws ExistentialException when transaction start fails
     */
    public static Tr begin(String op, Transaction custom) throws ExistentialException
    {
        return instance().begin(op, custom);
    }

    /**
     * Commits an existential transaction.
     * Performs validation of the rules configured for the transaction's business operation.
     * After commit, tranID becomes invalid.
     *
     * @param tranID transaction identifier
     * @throws ExistentialException when validation or commit fails
     */
    public static void commit(String tranID) throws ExistentialException
    {
        instance().commit(tranID);
    }

    /**
     * Commits an existential transaction.
     * Performs validation of the rules configured for the transaction's business operation.
     * After commit, tranID becomes invalid.
     *
     * @param tr transaction object
     * @throws ExistentialException when validation or commit fails
     */
    public static void commit(Tr tr) throws ExistentialException
    {
        instance().commit(tr);
    }

    /**
     * Creates a checkpoint in the transaction lifecycle.
     *
     * @param tranID transaction identifier
     * @throws ExistentialException when checkpoint fails
     */
    public static void checkpoint(String tranID) throws ExistentialException
    {
        instance().checkpoint(tranID);
    }

    /**
     * Creates a checkpoint in the transaction lifecycle.
     *
     * @param tr transaction object
     * @throws ExistentialException when checkpoint fails
     */
    public static void checkpoint(Tr tr) throws ExistentialException
    {
        instance().checkpoint(tr);
    }

    /**
     * Rolls back an existential transaction.
     * Rule validation is not performed.
     * After rollback, tranID becomes invalid.
     *
     * @param tranID transaction identifier
     * @throws ExistentialException when rollback fails
     */
    public static void rollback(String tranID) throws ExistentialException
    {
        instance().rollback(tranID);
    }

    /**
     * Rolls back an existential transaction.
     * Rule validation is not performed.
     * After rollback, tranID becomes invalid.
     *
     * @param tr transaction object
     * @throws ExistentialException when rollback fails
     */
    public static void rollback(Tr tr) throws ExistentialException
    {
        instance().rollback(tr);
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
        instance().event(event, t, type, tranID);
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
        instance().event(event, type, tranID);
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
    public static <T> void create(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().create(t, type, tranID);
    }

    /**
     * Variant of create() without type parameter. Only suitable for non-generic entity types.
     *
     * @param t entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void create(T t, String tranID) throws ExistentialException
    {
        instance().create(t, tranID);
    }

    /**
     * Emits Delete<T> event.
     *
     * @param t entity value
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void delete(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().delete(t, type, tranID);
    }

    /**
     * Variant of delete() without type parameter. Only suitable for non-generic entity types.
     *
     * @param t entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void delete(T t, String tranID) throws ExistentialException
    {
        instance().delete(t, tranID);
    }

    /**
     * Emits Modify<T> event.
     *
     * @param t entity value
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void modify(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().modify(t, type, tranID);
    }

    /**
     * Variant of modify() without type parameter. Only suitable for non-generic entity types.
     *
     * @param t entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void modify(T t, String tranID) throws ExistentialException
    {
        instance().modify(t, tranID);
    }

    /**
     * Emits Update<T> event.
     *
     * @param t entity value
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void update(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().update(t, type, tranID);
    }

    /**
     * Variant of update() without type parameter. Only suitable for non-generic entity types.
     *
     * @param t entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void update(T t, String tranID) throws ExistentialException
    {
        instance().update(t, tranID);
    }

    /**
     * Emits Change<T> event.
     *
     * @param t entity value
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void change(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().change(t, type, tranID);
    }

    /**
     * Variant of change() without type parameter. Only suitable for non-generic entity types.
     *
     * @param t entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void change(T t, String tranID) throws ExistentialException
    {
        instance().change(t, tranID);
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
    public static <T> void mutate(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().mutate(t0, t1, type, tranID);
    }

    /**
     * Variant of mutate() without type parameter. Only suitable for non-generic entity types.
     *
     * @param t0 previous entity value
     * @param t1 new entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void mutate(T t0, T t1, String tranID) throws ExistentialException
    {
        instance().mutate(t0, t1, tranID);
    }

    /**
     * Emits Port<T> event based on before- and after- entity states,
     * where one of these states may be null.
     *
     * @param t0 previous entity value
     * @param t1 new entity value
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void port(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().port(t0, t1, type, tranID);
    }

    /**
     * Variant of port() without type parameter. Only suitable for non-generic entity types.
     *
     * @param t0 previous entity value
     * @param t1 new entity value
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void port(T t0, T t1, String tranID) throws ExistentialException
    {
        instance().port(t0, t1, tranID);
    }

    /**
     * Emits a read event for the entity using an explicit type key.
     *
     * @param entity entity being read
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void read(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().read(entity, type, tranID);
    }

    /**
     * Emits a read event for the entity.
     *
     * @param entity entity being read
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void read(T entity, String tranID) throws ExistentialException
    {
        instance().read(entity, tranID);
    }

    /**
     * Emits a write event for the entity using an explicit type key.
     *
     * @param entity entity being written
     * @param type type key to use for dispatch
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void write(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().write(entity, type, tranID);
    }

    /**
     * Emits a write event for the entity.
     *
     * @param entity entity being written
     * @param tranID transaction identifier
     * @throws ExistentialException when event handling fails
     */
    public static <T> void write(T entity, String tranID) throws ExistentialException
    {
        instance().write(entity, tranID);
    }

    /**
     * Returns the current value of a library behavior flag.
     *
     * @param flag flag identifier from Flags
     * @return current flag value
     */
    public static boolean get(int flag)
    {
        return instance().get(flag);
    }

    /**
     * Enables the specified library behavior flag.
     *
     * @param flag flag identifier from Flags
     */
    public static void on(int flag)
    {
        instance().on(flag);
    }

    /**
     * Disables the specified library behavior flag.
     *
     * @param flag flag identifier from Flags
     */
    public static void off(int flag)
    {
        instance().off(flag);
    }

    /**
     * Flips the specified library behavior flag.
     *
     * @param flag flag identifier from Flags
     * @return updated flag value
     */
    public static boolean toggle(int flag)
    {
        return instance().toggle(flag);
    }

    /**
     * Closes the library instance and releases resources.
     */
    public static void close()
    {
        instance().close();
    }

    /**
     * Sets the configured status for the library.
     *
     * @param b configured flag value
     */
    public static void configured(boolean b)
    {
        instance().configured(b);
    }

    /**
     * Reports whether the library has completed configuration.
     *
     * @return true when configured
     */
    public static boolean configured()
    {
        return instance().configured();
    }

    /**
     * Returns the configuration subsystem for inspection or advanced usage.
     *
     * @return ExistentialConfigs instance
     */
    public static ExistentialConfigs configs()
    {
        return instance().configs();
    }

    /**
     * Returns the transaction subsystem for inspection or advanced usage.
     *
     * @return ExistentialTransactions instance
     */
    public static ExistentialTransactions transactions()
    {
        return instance().transactions();
    }
}
