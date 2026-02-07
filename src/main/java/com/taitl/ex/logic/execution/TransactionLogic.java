package com.taitl.ex.logic.execution;

import java.io.*;
import com.taitl.ex.domain.execution.*;
import com.taitl.ex.logic.existential.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.helper.*;
import com.taitl.existential.keys.*;

public class TransactionLogic implements Closeable
{
    protected ExistentialTransactions ee;
    protected OpRunRegistry registry;
    protected CreateOpRun createOpRun;

    public TransactionLogic(ExistentialTransactions ee)
    {
        this.ee = ee;
        this.registry = new OpRunRegistry(ee);
        this.createOpRun = new CreateOpRun(this);
    }

    public String begin(String op) throws ExistentialException
    {
        Args.cool(op, "op");
        OpKey.validate(op);
        OpRun tr = createOpRun.call(op);
        return tr.id.toString();
    }

    public void commit(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        OpRun tr = registry.get(tranID);
        if (tr == null)
        {
            throw new NotFoundException("Op transaction not found, id=" + tranID);
        }
        // TODO
        // Commit transactions - run handlers and evaluate validation expressions
        // Close transactions, remove op transaction from registry
    }

    public void check(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        // Run verification logic.
        // same as commit()
    }

    public void rollback(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        // TODO
        // Locate transaction in TransactionRegistry
        // Care for scenarios when tran is not found
        // Close transactions, remove op transaction from registry
    }

    // cleanup: Close transactions, remove op transaction from registry
    public void close()
    {
        registry.clear();
    }

    public Existential ex()
    {
        return ee.ex();
    }

    public OpRunRegistry registry()
    {
        return registry;
    }
}
