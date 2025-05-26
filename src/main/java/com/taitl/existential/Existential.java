package com.taitl.existential;

import com.taitl.existential.contexts.OpContext;
import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.keys.TypeKey;
import com.taitl.existential.logic.*;

import java.io.Closeable;

/**
 * Main entry point into Existential library. See documentation on how to use.
 * <p>
 *
 * Documentation:
 *   <a href="https://github.com/andreypotekhin/taitl-existential">Existential</a>
 *
 * @author Andrey Potekhin
 *
 * @see ExistentialTransactions
 * @see ExistentialEvents
 * @see ExistentialFlags
 * @see ExistentialContexts
 * @see ExistentialAccess
 */
public final class Existential implements Closeable
{
    private ExistentialTransactions transactions = new ExistentialTransactions(this);
    private ExistentialEvents events = new ExistentialEvents(this);
    private ExistentialFlags flags = new ExistentialFlags(this);
    private ExistentialContexts contexts = new ExistentialContexts(this);
    private ExistentialAccess access = new ExistentialAccess(this);

    private boolean configured = false;
    private boolean closed = false;

    public OpContext configure(String op)
    {
        return contexts.configure(op);
    }

    public String begin(String op) throws ExistentialException
    {
        return transactions.begin(op);
    }

    public void commit(String tranID) throws ExistentialException
    {
        transactions.commit(tranID);
    }

    public void checkpoint(String tranID) throws ExistentialException
    {
        transactions.checkpoint(tranID);
    }

    public void rollback(String tranID) throws ExistentialException
    {
        transactions.rollback(tranID);
    }

    public <T> void emit(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.emit(t0, t1, type, tranID);
    }

    public <T> void emit(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.emit(t, type, tranID);
    }

    public <T> void emit(T t0, T t1, String tranID) throws ExistentialException
    {
        events.emit(t0, t1, tranID);
    }

    public <T> void emit(T t, String tranID) throws ExistentialException
    {
        events.emit(t, tranID);
    }

    public <T> void read(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
        access.read(entity, type, tranID);
    }

    public <T> void read(T entity, String tranID) throws ExistentialException
    {
        access.read(entity, tranID);
    }

    public <T> void write(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
        access.write(entity, type, tranID);
    }

    public <T> void write(T entity, String tranID) throws ExistentialException
    {
        access.write(entity, tranID);
    }

    public void on(int flag)
    {
        flags.on(flag);
    }

    public void off(int flag)
    {
        flags.off(flag);
    }

    public void toggle(int flag)
    {
        flags.toggle(flag);
    }

    public boolean get(int flag)
    {
        return flags.get(flag);
    }

    public void close()
    {
        if (!closed)
        {
            transactions.close();
            events.close();
            flags.close();
            contexts.close();
            access.close();
            closed = true;
        }
    }

    public void configured(boolean b)
    {
        configured = b;
    }

    public boolean configured()
    {
        return configured;
    }

    public ExistentialContexts contexts()
    {
        return contexts;
    }
}
