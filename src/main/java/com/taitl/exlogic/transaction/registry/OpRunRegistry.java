package com.taitl.exlogic.transaction.registry;

import java.util.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.helper.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import com.taitl.exlogic.existential.*;
import com.taitl.exlogic.transaction.*;

/**
 * OpRunRegistry creates OpTrans and holds references to them
 * for the duration of a business transaction.
 * TODO: We don't need this class if just return OpRun object to caller of op.start()
 */
public class OpRunRegistry
{
    protected ExistentialExecution exec;
    protected Map<String, OpRun> reg = new LinkedHashMap<>();

    public OpRunRegistry(ExistentialExecution exec)
    {
        this.exec = exec;
    }

    public OpRun create(String op)
    {
        Args.cool(op, "op");
        OpKey.validate(op);
        OpRun o = new OpRun(op, generateId());

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

    public OpRun get(String id) throws NotFoundException
    {
        Args.cool(id, "id");
        OpRun o = reg.get(id);
        if (o == null)
        {
            throw new NotFoundException("Transaction not found, id=" + id);
        }
        return o;
    }

    public OpRun remove(String id) throws NotFoundException
    {
        Args.cool(id, "id");
        OpRun o = reg.get(id);
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
