package com.taitl.ex.logic.transactions;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.transactions.actions.*;
import com.taitl.ex.logic.transactions.data.*;
import com.taitl.ex.logic.validation.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.io.*;

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
    public EvaluationLogic evaluationLogic;
    public ValidationLogic validationLogic;

    public TransactionLogic(ExistentialTransactions ee)
    {
        this.ee = ee;
        this.createTran = new CreateTran(this);
        this.registry = new TrRegistry(this, createTran);
        this.beginTran = new BeginTran(this);
        this.commitTran = new CommitTran(this);
        this.checkpointTran = new CheckpointTran(this);
        this.rollbackTran = new RollbackTran(this);
        this.disposeTran = new DisposeTran(this);
        this.validationLogic = Creator.create(ValidationLogic.class, new Class[] { TransactionLogic.class }, this);
        this.evaluationLogic = Creator.create(EvaluationLogic.class, new Class[] { TransactionLogic.class }, this);
    }

    public Tr begin(String op) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        ee.ex().configs().done();
        Tr tr = registry.create(op, null);
        beginTran.call(tr);
        return tr;
    }

    public Tr begin(String op, Transaction custom) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        ee.ex().configs().done();
        Tr tr = registry.create(op, custom);
        beginTran.call(tr);
        return tr;
    }

    public void checkpoint(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        checkpoint(tr(tranID));
    }

    public void checkpoint(Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        checkpointTran.call(tr);
    }

    public void commit(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        commit(tr(tranID));
    }

    public void commit(Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        commitTran.call(tr);
        disposeTran.call(tr);
    }

    public void rollback(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        rollback(tr(tranID));
    }

    public void rollback(Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
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
