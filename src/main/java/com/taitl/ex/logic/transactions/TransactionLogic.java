package com.taitl.ex.logic.transactions;

import java.io.*;
import com.taitl.ex.common.helper.*;
import com.taitl.ex.core.execution.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.execution.actions.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

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
        sane(op, "op");
        OpKey.validate(op);
        OpRun tr = createOpRun.call(op, null);
        return tr.id.toString();
    }

    public String begin(String op, Transaction custom) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        OpRun tr = createOpRun.call(op, custom);
        return tr.id.toString();
    }

    public void commit(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        OpRun tr = registry.get(tranID);
        State.verify(tr != null, "Transaction not found, id=" + tranID);
        // TODO
        // Commit transactions - run handlers and evaluate validation expressions
        // Close transactions, remove OpRun from the registry
    }

    public void checkpoint(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        // TODO
        // Run verification logic.
        // same as commit()
    }

    public void rollback(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        // TODO
        // Locate transaction in TransactionRegistry
        // Care for scenarios when tran is not found
        // Close transactions, remove OpRun from the registry
    }

    public Existential ex()
    {
        return ee.ex();
    }

    public OpRunRegistry registry()
    {
        return registry;
    }

    /** Close on exit */
    public void close()
    {
        registry.clear();
    }
}
