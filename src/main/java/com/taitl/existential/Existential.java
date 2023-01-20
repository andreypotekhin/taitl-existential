package com.taitl.existential;

import com.taitl.existential.constants.Flags;
import com.taitl.existential.contexts.OpContext;
import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.helper.Args;
import com.taitl.existential.keys.TypeKey;

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
    public ExistentialTransactions transactions = new ExistentialTransactions(this);
    public ExistentialEvents events = new ExistentialEvents(this);
    public ExistentialFlags flags = new ExistentialFlags(this);
    public ExistentialContexts contexts = new ExistentialContexts(this);
    public ExistentialAccess access = new ExistentialAccess(this);
    public boolean finalized = false;
    public boolean closed = false;

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

    public <T> void send(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.send(t0, t1, type, tranID);
    }

    public <T> void send(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.send(t, type, tranID);
    }

    public <T> void send(T t0, T t1, String tranID) throws ExistentialException
    {
        events.send(t0, t1, tranID);
    }

    public <T> void send(T t, String tranID) throws ExistentialException
    {
        events.send(t, tranID);
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
}
