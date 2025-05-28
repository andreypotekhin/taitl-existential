package com.taitl.exlogic.existential;

import java.io.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.helper.*;
import com.taitl.existential.keys.*;
import com.taitl.exlogic.execution.*;

public class ExistentialExecution implements Closeable
{
    protected Existential ex;
    protected Executions executions;

    public ExistentialExecution(Existential ex)
    {
        this.ex = ex;
        this.executions = new Executions(this);
    }

    public String begin(String op) throws ExistentialException
    {
        Args.cool(op, "op");
        OpKey.validate(op);
        return executions.begin(op);
    }

    public void commit(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        executions.commit(tranID);
    }

    public void check(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        executions.check(tranID);
    }

    public void rollback(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        executions.rollback(tranID);
    }

    // cleanup: Close transactions, remove op transaction from registry

    public void close()
    {
        executions.close();
    }

    public Existential ex()
    {
        return ex;
    }
}
