package com.taitl.ex.logic.transactions;

import java.io.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.transactions.actions.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class TransactionLogic implements Closeable
{
    protected ExistentialTransactions ee;
    protected TrRegistry registry;
    protected CreateTr createTr;

    public TransactionLogic(ExistentialTransactions ee)
    {
        this.ee = ee;
        this.registry = new TrRegistry(ee);
        this.createTr = new CreateTr(this);
    }

    public String begin(String op) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        ee.ex().configs().finalizeConfiguration();
        Tr tr = createTr.call(op, null);
        return tr.id.toString();
    }

    public String begin(String op, Transaction custom) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        ee.ex().configs().finalizeConfiguration();
        Tr tr = createTr.call(op, custom);
        return tr.id.toString();
    }

    public void commit(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        Tr tr = registry.get(tranID);
        verify(tr != null, "Transaction not found, id=" + tranID);
        // TODO
        // Commit transactions - run handlers and evaluate validation expressions
        // Close transactions, remove Tr from the registry
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
        // Close transactions, remove Tr from the registry
    }

    public Existential ex()
    {
        return ee.ex();
    }

    public TrRegistry registry()
    {
        return registry;
    }

    /** Close on exit */
    public void close()
    {
        registry.clear();
    }
}
