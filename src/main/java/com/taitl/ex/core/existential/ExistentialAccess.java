package com.taitl.ex.core.existential;

import java.io.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;

public class ExistentialAccess implements Closeable
{
    protected Existential ex;

    public ExistentialAccess(Existential ex)
    {
        this.ex = ex;
    }

    public <T> void read(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
        // TODO
    }

    public <T> void read(T entity, String tranID) throws ExistentialException
    {
        // TODO
    }

    public <T> void write(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
        // TODO
    }

    public <T> void write(T entity, String tranID) throws ExistentialException
    {
        // TODO
    }

    public void close()
    {
        // TODO
    }
}
