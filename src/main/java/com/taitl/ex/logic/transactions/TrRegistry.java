package com.taitl.ex.logic.transactions;

import java.util.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.configuration.actions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * TrRegistry creates Trs and holds references to them (keyed by Tr UUID string id)
 * for the duration of a business transaction.
 */
public class TrRegistry
{
    /** Tr id to Tr */
    protected Map<String, Tr> reg = new LinkedHashMap<>();

    protected ExistentialTransactions exec;
    protected CreateTransaction createTransaction = new CreateTransaction();

    public TrRegistry(ExistentialTransactions exec)
    {
        this.exec = exec;
    }

    public Tr create(String op, Transaction custom)
    {
        sane(op, "op");
        OpKey.validate(op);
        Tr o = createTransaction.forContexts(op, exec.ex().contexts().getContexts(op), custom);
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
}
