package com.taitl.ex.core.existential;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.io.*;

import static com.taitl.ex.common.helper.Args.*;

public class ExistentialTransactions implements Closeable
{
    @Up
    protected Existential ex;

    @Logic
    protected TransactionLogic transactionLogic;

    public ExistentialTransactions(Existential ex)
    {
        this.ex = ex;
        this.transactionLogic = new TransactionLogic(this);
    }

    public Tr begin(String op) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        return transactionLogic.begin(op);
    }

    public Tr begin(String op, Transaction custom) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        return transactionLogic.begin(op, custom);
    }

    public void commit(Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        tr.commit();
    }

    public void checkpoint(Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        tr.checkpoint();
    }

    public void rollback(Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        tr.rollback();
    }

    public Tr tr(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        return transactionLogic.tr(tranID);
    }

    /* Attributes */
    public Existential ex()
    {
        return ex;
    }

    public TransactionLogic logic()
    {
        return transactionLogic;
    }

    /* Cleanup */

    public void close()
    {
        ex.close();
    }
}
