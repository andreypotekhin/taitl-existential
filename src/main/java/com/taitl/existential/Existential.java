package com.taitl.existential;

import java.io.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

/**
 * Entry point into Existential library.
 *
 * Documentation:
 *   <a href="https://github.com/andreypotekhin/taitl-existential">Existential</a>
 *
 * @author Andrey Potekhin
 */
public final class Existential implements Closeable
{
    private ExistentialInit init = new ExistentialInit(this);
    private ExistentialAccess access = new ExistentialAccess(this);
    private ExistentialTransactions transactions = new ExistentialTransactions(this);
    private ExistentialEvents events = new ExistentialEvents(this);
    private ExistentialFlags flags = new ExistentialFlags(this);
    private ExistentialConfigs configs = new ExistentialConfigs(this);

    private boolean configured = false;
    private boolean closed = false;

    public ConfigBuilder configure(String op)
    {
        return configs.get(op);
    }

    public String begin(String op) throws ExistentialException
    {
        return transactions.begin(op);
    }

    public String begin(String op, Transaction custom) throws ExistentialException
    {
        return transactions.begin(op, custom);
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

    public boolean get(int flag)
    {
        return flags.get(flag);
    }

    public void on(int flag)
    {
        flags.on(flag);
    }

    public void off(int flag)
    {
        flags.off(flag);
    }

    public boolean toggle(int flag)
    {
        return flags.toggle(flag);
    }

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

    public void configured(boolean b)
    {
        configured = b;
    }

    public boolean configured()
    {
        return configured;
    }

    public ExistentialConfigs configs()
    {
        return configs;
    }

    public Contexts contexts()
    {
        return configs.contexts();
    }

    public ExistentialTransactions transactions()
    {
        return transactions;
    }
}
