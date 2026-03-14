package com.taitl.ex.logic.transactions.data;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.ex.logic.transactions.actions.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;
import java.util.concurrent.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * TrRegistry creates Trs and holds references to them (keyed by Tr UUID string id)
 * for the duration of a business transaction.
 */
public class TrRegistry
{
    public static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#transaction-not-found";
    protected Map<String, Tr> reg = new ConcurrentHashMap<>();
    protected CreateTran createTran;
    @Up
    protected TransactionLogic tl;

    public TrRegistry(TransactionLogic tl, CreateTran createTran)
    {
        sane(tl, "tl", createTran, "createTran");
        this.tl = tl;
        this.createTran = createTran;
    }

    public Tr create(String op, Transaction custom)
    {
        sane(op, "op");
        OpKey.validate(op);
        Tr o = createTran.forConfig(op, tl.ex().configs().config(op), custom);
        reg.put(o.id.toString(), o);
        return o;
    }

    public Tr get(String id) throws NotFoundException
    {
        sane(id, "id");
        Tr o = reg.get(id);
        if (o == null)
        {
            throw new NotFoundException(String.format("Transaction not found, id=%s. See %s",
                    id, TROUBLESHOOTING_SECTION));
        }
        return o;
    }

    public Tr remove(String id) throws NotFoundException
    {
        sane(id, "id");
        Tr o = reg.remove(id);
        if (o == null)
        {
            throw new NotFoundException(String.format("Transaction not found, id=%s. See %s",
                    id, TROUBLESHOOTING_SECTION));
        }
        return o;
    }

    public void clear()
    {
        reg.clear();
    }
}
