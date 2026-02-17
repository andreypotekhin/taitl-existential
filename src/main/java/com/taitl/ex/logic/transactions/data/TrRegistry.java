package com.taitl.ex.logic.transactions.data;

import java.util.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.ex.logic.transactions.actions.*;
import com.taitl.existential.configs.*;
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
    protected Map<String, Tr> reg = new LinkedHashMap<>();
    protected CreateTran createTran;
    protected TransactionLogic tl;

    public TrRegistry(TransactionLogic tl)
    {
        sane(tl, "tl");
        this.tl = tl;
    }

    public Tr create(String op, Transaction custom)
    {
        sane(op, "op");
        OpKey.validate(op);
        Tr o = createTran.forConfig(op, tl.ex().configs().config(op), custom);
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
