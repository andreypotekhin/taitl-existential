package com.taitl.ex.logic.existential;

import java.io.*;
import com.taitl.ex.logic.execution.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.helper.*;
import com.taitl.existential.keys.*;

public class ExistentialTransactions implements Closeable
{
    protected Existential ex;
    protected TransactionLogic transactionLogic;

    public ExistentialTransactions(Existential ex)
    {
        this.ex = ex;
        this.transactionLogic = new TransactionLogic(this);
    }

    public String begin(String op) throws ExistentialException
    {
        Args.cool(op, "op");
        OpKey.validate(op);
        return transactionLogic.begin(op);
    }

    public void commit(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        transactionLogic.commit(tranID);
    }

    public void check(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        transactionLogic.check(tranID);
    }

    public void rollback(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        transactionLogic.rollback(tranID);
    }

    // cleanup: Close transactions, remove op transaction from registry

    public void close()
    {
        transactionLogic.close();
    }

    public Existential ex()
    {
        return ex;
    }
}
