package com.taitl.ex.core.existential;

import java.io.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

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

    public String begin(String op) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        return transactionLogic.begin(op);
    }

    public String begin(String op, Transaction custom) throws ExistentialException
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

    public void checkpoint(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        transactionLogic.checkpoint(tranID);
    }

    public void rollback(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        transactionLogic.rollback(tranID);
    }

    public Tr tr(String tranID) throws ExistentialException
    {
        sane(tranID, "tranID");
        return transactionLogic.tr(tranID);
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
