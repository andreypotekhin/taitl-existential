package com.taitl.existential.op.trans;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.taitl.existential.contexts.Context;
import com.taitl.existential.contexts.Contexts;
import com.taitl.existential.exceptions.NotFoundException;
import com.taitl.existential.helper.Args;
import com.taitl.existential.transactions.Transaction;

/**
 * TransactionRegistry creates OpTrans and holds references to them
 * for the duration of a business transaction.
 */
public class TransactionRegistry
{
    protected Map<String, OpTran> reg = new LinkedHashMap<>();

    public OpTran create(String op)
    {
        Args.cool(op, "op");
        OpTran o = new OpTran(op, generateId());

        for (Context context : Contexts.getcreate(op))
        {
            for (Transaction tr : context.createTransactions())
            {
                o.transactions.add(tr);
            }
        }

        synchronized (reg)
        {
            reg.put(op, o);
        }
        return o;
    }

    public OpTran get(String id) throws NotFoundException
    {
        Args.cool(id, "id");
        OpTran o = reg.get(id);
        if (o == null)
        {
            throw new NotFoundException("Transaction not found, id=" + id);
        }
        return o;
    }

    public OpTran remove(String id) throws NotFoundException
    {
        Args.cool(id, "id");
        OpTran o = reg.get(id);
        if (o == null)
        {
            throw new NotFoundException("Transaction not found, id=" + id);
        }
        synchronized (reg)
        {
            reg.remove(id);
        }
        return o;
    }

    protected UUID generateId()
    {
        return UUID.randomUUID();
    }
}
