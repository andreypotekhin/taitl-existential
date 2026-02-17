package com.taitl.existential;

import com.taitl.ex.core.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Static facade for Existential library.
 * Holds an instance of Existential class, to which it delegates all calls.
 * Note: this class is only a convenience / shorthand for accessing the methods
 * of Existential class.
 * You pay for this convenience by assuming only a single Existential instance per JVM.
 * This is ok for most applications, but may become an issue if you're writing library code.
 *
 * Usage:
 * Ex.configure("api/resource").context("api/resource/create")...
 *
 * Documentation:
 *   <a href="https://github.com/andreypotekhin/taitl-existential">Existential</a>
 *
 * @author Andrey Potekhin
 *
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
     * Instance setter, for testing purposes.
     * Note: this method is not envisioned for use in production.
     * @return Previous instance
     */
    public static Existential instance(Existential instance)
    {
        sane(instance, "instance");
        Existential prevInstance = InstanceHolder.instance;
        InstanceHolder.instance = instance;
        return prevInstance;
    }

    public static ConfigBuilder configure(String op)
    {
        return instance().configure(op);
    }

    public static String begin(String op) throws ExistentialException
    {
        return instance().begin(op);
    }

    public static String begin(String op, Transaction custom) throws ExistentialException
    {
        return instance().begin(op, custom);
    }

    /**
     * Commits an existential transaction.
     * Performs validation of the rules configured for transaction's business op.
     * Note: after the commit(), tranID becomes invalid
     */
    public static void commit(String tranID) throws ExistentialException
    {
        instance().commit(tranID);
    }

    public static void checkpoint(String tranID) throws ExistentialException
    {
        instance().checkpoint(tranID);
    }

    /**
     * Rolls back an existential transaction.
     * Rule validation is not performed.
     * Note: after the rollback(), tranID becomes invalid
     */
    public static void rollback(String tranID) throws ExistentialException
    {
        instance().rollback(tranID);
    }

    public static <T> void event(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().event(t0, t1, type, tranID);
    }

    public static <T> void event(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().event(t, type, tranID);
    }

    public static <T> void event(T t0, T t1, String tranID) throws ExistentialException
    {
        instance().event(t0, t1, tranID);
    }

    public static <T> void event(T t, String tranID) throws ExistentialException
    {
        instance().event(t, tranID);
    }

    public static <T> void read(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().read(entity, type, tranID);
    }

    public static <T> void read(T entity, String tranID) throws ExistentialException
    {
        instance().read(entity, tranID);
    }

    public static <T> void write(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
        instance().write(entity, type, tranID);
    }

    public static <T> void write(T entity, String tranID) throws ExistentialException
    {
        instance().write(entity, tranID);
    }

    public static boolean get(int flag)
    {
        return instance().get(flag);
    }

    public static void on(int flag)
    {
        instance().on(flag);
    }

    public static void off(int flag)
    {
        instance().off(flag);
    }

    public static boolean toggle(int flag)
    {
        return instance().toggle(flag);
    }

    public static void close()
    {
        instance().close();
    }

    public static void configured(boolean b)
    {
        instance().configured(b);
    }

    public static boolean configured()
    {
        return instance().configured();
    }

    public static ExistentialConfigs configs()
    {
        return instance().configs();
    }

    public static ExistentialTransactions transactions()
    {
        return instance().transactions();
    }
}
