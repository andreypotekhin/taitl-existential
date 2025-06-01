package com.taitl.existential;

import java.io.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.ops.*;
import com.taitl.exlogic.contexts.*;
import com.taitl.exlogic.existential.*;

/**
 * Main entry point into Existential library.
 *
 * Documentation:
 *   <a href="https://github.com/andreypotekhin/taitl-existential">Existential</a>
 *
 * @author Andrey Potekhin
 *
 * @see ExistentialExecution
 * @see ExistentialEvents
 * @see ExistentialFlags
 * @see ExistentialOps
 * @see ExistentialAccess
 */
public final class Existential implements Closeable
{
    private ExistentialAccess access = new ExistentialAccess(this);
    private ExistentialExecution transactions = new ExistentialExecution(this);
    private ExistentialEvents events = new ExistentialEvents(this);
    private ExistentialFlags flags = new ExistentialFlags(this);
    private ExistentialOps ops = new ExistentialOps(this);

    private boolean configured = false;
    private boolean closed = false;

    public Op op(String op)
    {
        return ops.configure(op);
    }

    public String begin(String op) throws ExistentialException
    {
        return transactions.begin(op);
    }

    public void commit(String tranID) throws ExistentialException
    {
        transactions.commit(tranID);
    }

    public void check(String tranID) throws ExistentialException
    {
        transactions.check(tranID);
    }

    public void rollback(String tranID) throws ExistentialException
    {
        transactions.rollback(tranID);
    }

    public <T> void event(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.event(t0, t1, type, tranID);
    }

    public <T> void event(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        events.event(t, type, tranID);
    }

    public <T> void event(T t0, T t1, String tranID) throws ExistentialException
    {
        events.event(t0, t1, tranID);
    }

    public <T> void event(T t, String tranID) throws ExistentialException
    {
        events.event(t, tranID);
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
            ops.close();
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

    public ExistentialOps ops()
    {
        return ops;
    }

    public Contexts contexts()
    {
        return ops.contexts();
    }

    public ExistentialExecution transactions()
    {
        return transactions;
    }
}
