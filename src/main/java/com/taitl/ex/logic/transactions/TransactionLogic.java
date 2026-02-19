package com.taitl.ex.logic.transactions;

import java.io.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.transactions.actions.*;
import com.taitl.ex.logic.transactions.data.*;
import com.taitl.ex.logic.validation.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class TransactionLogic implements Closeable
{
    protected ExistentialTransactions ee;
    protected TrRegistry registry;
    protected CreateTran createTran;
    protected BeginTran beginTran;
    protected CommitTran commitTran;
    protected CheckpointTran checkpointTran;
    protected RollbackTran rollbackTran;
    protected DisposeTran disposeTran;
    public ValidationLogic validationLogic;

    public TransactionLogic(ExistentialTransactions ee)
    {
        this.ee = ee;
        this.registry = new TrRegistry(this);
        this.createTran = new CreateTran(this);
        this.beginTran = new BeginTran(this);
        this.commitTran = new CommitTran(this);
        this.checkpointTran = new CheckpointTran(this);
        this.rollbackTran = new RollbackTran(this);
        this.disposeTran = new DisposeTran(this);
        this.validationLogic = new ValidationLogic(this);
    }

    public String begin(String op) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        ee.ex().configs().done();
        Tr tr = registry.create(op, null);
        beginTran.call(tr);
        return tr.id.toString();
    }

    public String begin(String op, Transaction custom) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        ee.ex().configs().done();
        Tr tr = registry.create(op, custom);
        beginTran.call(tr);
        return tr.id.toString();
    }

    public void checkpoint(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        Tr tr = tr(tranID);
        checkpointTran.call(tr);
    }

    public void commit(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        Tr tr = tr(tranID);
        commitTran.call(tr);
        disposeTran.call(tr);
    }

    public void rollback(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        Tr tr = tr(tranID);
        rollbackTran.call(tr);
        disposeTran.call(tr);
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
        return registry.get(tranID);
    }

    /** Close on exit */
    public void close()
    {
        registry.clear();
    }
}
