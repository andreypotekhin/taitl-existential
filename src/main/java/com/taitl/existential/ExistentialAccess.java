package com.taitl.existential;

import com.taitl.existential.exceptions.ExistentialException;

public class ExistentialAccess
{
    protected Existential ex;

    public ExistentialAccess(Existential ex)
    {
        this.ex = ex;
    }

    public <T> void read(T entity, String tranID) throws ExistentialException
    {
    }

    public <T> void write(T entity, String tranID) throws ExistentialException
    {
    }
}
