package com.taitl.ex.core.execution;

import java.util.*;
import com.taitl.ex.core.existential.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * TrRegistry creates Trs and holds references to them (keyed by Tr UUID string)
 * for the duration of a business transaction.
 * TODO: We don't need this class if just return Tr object to the caller of op.start()
 */
public class TrRegistry
{
    /** Tr id to Tr */
    protected Map<String, Tr> reg = new LinkedHashMap<>();

    protected ExistentialTransactions exec;

    public TrRegistry(ExistentialTransactions exec)
    {
        this.exec = exec;
    }

    public Tr create(String op, Transaction custom)
    {
        sane(op, "op");
        OpKey.validate(op);
        Tr o = new Tr(op, generateId());

        for (Context context : exec.ex().contexts().getContexts(op))
        {
            o.addTransaction(context.createTransaction());
        }
        if (custom != null)
        {
            o.addTransaction(custom);
        }
        synchronized (this)
        {
            reg.put(o.id.toString(), o);
        }
        return o;
    }

    public Tr get(String id) throws NotFoundException
    {
        sane(id, "id");
        Tr o = reg.get(id);
        if (o == null)
        {
            throw new NotFoundException("Transaction not found, id=" + id);
        }
        return o;
    }

    public Tr remove(String id) throws NotFoundException
    {
        sane(id, "id");
        Tr o = reg.get(id);
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
