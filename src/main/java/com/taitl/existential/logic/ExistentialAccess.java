package com.taitl.existential.logic;

import com.taitl.existential.*;
import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.keys.TypeKey;

import java.io.Closeable;

public class ExistentialAccess implements Closeable
{
    protected Existential ex;

    public ExistentialAccess(Existential ex)
    {
        this.ex = ex;
    }

    public <T> void read(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
    }

    public <T> void read(T entity, String tranID) throws ExistentialException
    {
    }

    public <T> void write(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
    }

    public <T> void write(T entity, String tranID) throws ExistentialException
    {
    }

    public void close()
    {
    }
}
