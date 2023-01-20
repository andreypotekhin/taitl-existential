package com.taitl.existential;

import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.exceptions.NotFoundException;
import com.taitl.existential.helper.Args;
import com.taitl.existential.keys.OpKey;
import com.taitl.existential.transactions.OpTransaction;
import com.taitl.existential.transactions.OpTransactionRegistry;

import java.io.Closeable;

public class ExistentialTransactions implements Closeable
{
    protected Existential ex;
    protected OpTransactionRegistry registry = new OpTransactionRegistry();

    public ExistentialTransactions(Existential ex)
    {
        this.ex = ex;
    }

    public String begin(String op) throws ExistentialException
    {
        Args.cool(op, "op");
        OpKey.requireValidName(op);
        ex.contexts.finalizeSetup();
        OpTransaction opTran = registry.create(op);
        return opTran.id.toString();
    }

    public void commit(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        OpTransaction opTran = registry.get(tranID);
        if (opTran == null)
        {
            throw new NotFoundException("Op transaction not found, id=" + tranID);
        }
        // TODO
        // Commit transactions - run handlers and evaluate validation expressions
        // Close transactions, remove op transaction from registry
    }

    public void checkpoint(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        // same as commit()
    }

    public void rollback(String tranID) throws ExistentialException
    {
        Args.cool(tranID, "tranID");
        // TODO
        // Find op transaction in TranactionRegistry
        // Care for scenarios when tran is not found
        // Close transactions, remove op transaction from registry
    }

    // cleanup: Close transactions, remove op transaction from registry

    public void close()
    {
    }
}
