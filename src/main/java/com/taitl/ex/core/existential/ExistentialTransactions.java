package com.taitl.ex.core.existential;

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
    protected Existential ex;
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

    public void commit(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        transactionLogic.commit(tranID);
    }

    public void commit(Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        transactionLogic.commit(tr);
    }

    public void checkpoint(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        transactionLogic.checkpoint(tranID);
    }

    public void checkpoint(Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        transactionLogic.checkpoint(tr);
    }

    public void rollback(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        transactionLogic.rollback(tranID);
    }

    public void rollback(Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        transactionLogic.rollback(tr);
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
        transactionLogic.close();
    }
}
