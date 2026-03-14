package com.taitl.ex.concrete;

import com.taitl.ex.logic.events.*;
import com.taitl.ex.logic.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class ConcreteTrBuilder
{
    com.taitl.existential.transactions.Tr tr;
    String op;
    UUID id;
    TransactionLogic tl;
    EventLogic el;

    public ConcreteTr build()
    {
        sane(tr, "tr", op, "op", id, "id", tl, "tl", el, "el");
        return new ConcreteTr(tr, op, id, tl, el);
    }

    public ConcreteTrBuilder tr(com.taitl.existential.transactions.Tr tr)
    {
        sane(tr, "tr");
        this.tr = tr;
        return this;
    }

    public ConcreteTrBuilder op(String op)
    {
        sane(op, "op");
        this.op = op;
        return this;
    }

    public ConcreteTrBuilder id(UUID id)
    {
        sane(id, "id");
        this.id = id;
        return this;
    }

    public ConcreteTrBuilder tl(TransactionLogic tl)
    {
        sane(tl, "tl");
        this.tl = tl;
        return this;
    }

    public ConcreteTrBuilder el(EventLogic el)
    {
        sane(el, "el");
        this.el = el;
        return this;
    }
}
