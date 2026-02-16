package com.taitl.ex.logic.transactions;

import java.io.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.transactions.actions.*;
import com.taitl.ex.logic.transactions.data.*;
import com.taitl.ex.logic.validation.*;
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
    protected CreateTransaction createTransaction;
    protected BeginTransaction beginTransaction;
    protected CommitTransaction commitTransaction;
    protected CheckpointTransaction checkpointTransaction;
    protected RollbackTransaction rollbackTransaction;
    protected DisposeTransaction disposeTransaction;
    public ValidationLogic validationLogic;

    public TransactionLogic(ExistentialTransactions ee)
    {
        this.ee = ee;
        this.registry = new TrRegistry(ee);
        this.createTransaction = new CreateTransaction(this);
        this.beginTransaction = new BeginTransaction(this);
        this.commitTransaction = new CommitTransaction(this);
        this.checkpointTransaction = new CheckpointTransaction(this);
        this.rollbackTransaction = new RollbackTransaction(this);
        this.disposeTransaction = new DisposeTransaction(this);
        this.validationLogic = new ValidationLogic(this);
    }

    public String begin(String op) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        ee.ex().configs().finalizeConfiguration();
        Tr tr = createTransaction.call(op, null);
        beginTransaction.call(tr);
        return tr.id.toString();
    }

    public String begin(String op, Transaction custom) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        ee.ex().configs().finalizeConfiguration();
        Tr tr = createTransaction.call(op, custom);
        beginTransaction.call(tr);
        return tr.id.toString();
    }

    public void checkpoint(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        Tr tr = registry.get(tranID);
        verify(tr != null, "Transaction not found, id=" + tranID);
        checkpointTransaction.call(tr);
    }

    public void commit(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        Tr tr = registry.get(tranID);
        verify(tr != null, "Transaction not found, id=" + tranID);
        commitTransaction.call(tr);
        disposeTransaction.call(tr);
    }

    public void rollback(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        Tr tr = registry.get(tranID);
        verify(tr != null, "Transaction not found, id=" + tranID);
        rollbackTransaction.call(tr);
        disposeTransaction.call(tr);
    }

    public Existential ex()
    {
        return ee.ex();
    }

    public TrRegistry registry()
    {
        return registry;
    }

    public Tr tr(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        Tr tr = registry.get(tranID);
        verify(tr != null, "Transaction not found, id=" + tranID);
        return tr;
    }

    /** Close on exit */
    public void close()
    {
        registry.clear();
    }
}
