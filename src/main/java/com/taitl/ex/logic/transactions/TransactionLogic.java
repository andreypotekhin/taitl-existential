package com.taitl.ex.logic.transactions;

import java.io.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.transactions.actions.*;
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
    protected CreateTr createTr;
    protected BeginTr beginTr;
    protected DisposeTr disposeTr;
    protected ValidationLogic validationLogic;

    public TransactionLogic(ExistentialTransactions ee)
    {
        this.ee = ee;
        this.registry = new TrRegistry(ee);
        this.createTr = new CreateTr(this);
        this.beginTr = new BeginTr(this);
        this.disposeTr = new DisposeTr(this);
        this.validationLogic = new ValidationLogic(this);
    }

    public String begin(String op) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        ee.ex().configs().finalizeConfiguration();
        Tr tr = createTr.call(op, null);
        beginTr.call(tr);
        return tr.id.toString();
    }

    public String begin(String op, Transaction custom) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        ee.ex().configs().finalizeConfiguration();
        Tr tr = createTr.call(op, custom);
        beginTr.call(tr);
        return tr.id.toString();
    }

    public void checkpoint(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        Tr tr = registry.get(tranID);
        verify(tr != null, "Transaction not found, id=" + tranID);
        validationLogic.run(tr);
    }

    public void commit(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        Tr tr = registry.get(tranID);
        verify(tr != null, "Transaction not found, id=" + tranID);
        validationLogic.run(tr);
        disposeTr.call(tr);
    }

    public void rollback(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        Tr tr = registry.get(tranID);
        verify(tr != null, "Transaction not found, id=" + tranID);
        disposeTr.call(tr);
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
