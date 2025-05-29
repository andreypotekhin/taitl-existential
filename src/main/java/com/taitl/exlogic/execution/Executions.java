package com.taitl.exlogic.execution;

import java.io.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.helper.*;
import com.taitl.existential.keys.*;
import com.taitl.exlogic.existential.*;
import com.taitl.exlogic.transaction.*;
import com.taitl.exlogic.transaction.registry.*;

public class Executions implements Closeable
{
    protected ExistentialExecution ee;
    protected RuntimeTransactionRegistry registry = new RuntimeTransactionRegistry();
    protected CreateRuntimeTransaction createRuntimeTransaction;

    public Executions(ExistentialExecution ee)
    {
        this.ee = ee;
        this.createRuntimeTransaction = new CreateRuntimeTransaction(this);
    }

    public String begin(String op) throws ExistentialException
    {
        Args.cool(op, "op");
        OpKey.validate(op);
        RuntimeTransaction tr = createRuntimeTransaction.call(op);
        return tr.id.toString();
    }

    public void commit(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        RuntimeTransaction tr = registry.get(tranID);
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

    public RuntimeTransactionRegistry registry()
    {
        return registry;
    }
}
