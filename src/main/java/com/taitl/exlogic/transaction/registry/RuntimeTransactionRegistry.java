package com.taitl.exlogic.transaction.registry;

import java.util.*;
import com.taitl.existential.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.helper.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import com.taitl.exlogic.existential.*;
import com.taitl.exlogic.transaction.*;

/**
 * TransactionRegistry creates OpTrans and holds references to them
 * for the duration of a business transaction.
 */
public class RuntimeTransactionRegistry
{
    protected ExistentialExecution exec;
    protected Map<String, RuntimeTransaction> reg = new LinkedHashMap<>();

    public RuntimeTransactionRegistry(ExistentialExecution exec)
    {
        this.exec = exec;
    }

    public RuntimeTransaction create(String op)
    {
        Args.cool(op, "op");
        OpKey.validate(op);
        RuntimeTransaction o = new RuntimeTransaction(op, generateId());

        for (Context context : exec.ex().contexts().createContexts(op))
        {
            for (Transaction tr : context.createTransactions())
            {
                o.addTransaction(tr);
            }
        }

        synchronized (this)
        {
            reg.put(o.id.toString(), o);
        }
        return o;
    }

    public RuntimeTransaction get(String id) throws NotFoundException
    {
        Args.cool(id, "id");
        RuntimeTransaction o = reg.get(id);
        if (o == null)
        {
            throw new NotFoundException("Transaction not found, id=" + id);
        }
        return o;
    }

    public RuntimeTransaction remove(String id) throws NotFoundException
    {
        Args.cool(id, "id");
        RuntimeTransaction o = reg.get(id);
        if (o == null)
        {
            throw new NotFoundException("Transaction not found, id=" + id);
        }
        synchronized (this)
        {
            reg.remove(id);
        }
        return o;
    }

    public void clear()
    {
        synchronized (this)
        {
            reg.clear();
        }
    }

    protected UUID generateId()
    {
        return UUID.randomUUID();
    }
}
