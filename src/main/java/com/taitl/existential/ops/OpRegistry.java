package com.taitl.existential.ops;

import java.util.*;
import java.util.stream.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.creator.*;
import com.taitl.existential.exceptions.NotFoundException;
import com.taitl.existential.helper.*;
import com.taitl.existential.keys.*;
import com.taitl.exlogic.existential.*;

/**
 * OpRegistry holds references to OpContexts.
 */
public class OpRegistry
{
    protected ExistentialOps ops;
    protected Map<String, Op> reg = new LinkedHashMap<>();

    public OpRegistry(ExistentialOps ops)
    {
        this.ops = ops;
    }

    public Op create(String name)
    {
        Args.cool(name, "name");
        Op o = new Op(name);
        synchronized (this)
        {
            for (Context context : ops.ex().contexts().createContexts(name))
            {
                o.addContext(context);
            }
            reg.put(name, o);
        }
        return o;
    }

    public boolean has(String id)
    {
        return reg.containsKey(id);
    }

    public Op get(String id) throws NotFoundException
    {
        Args.cool(id, "id");
        Op o = reg.get(id);
        if (o == null)
        {
            throw new NotFoundException("Context not found, id=" + id);
        }
        return o;
    }

    public Op getcreate(String id)
    {
        Args.cool(id, "id");
        Op o = reg.get(id);
        return (o != null) ? o : create(id);
    }

    public Op remove(String id) throws NotFoundException
    {
        Args.cool(id, "id");
        Op o = reg.get(id);
        if (o == null)
        {
            throw new NotFoundException("Context not found, id=" + id);
        }
        synchronized (reg)
        {
            reg.remove(id);
        }
        return o;
    }

    public void createSubcontexts()
    {
        reg.forEach((key, op) -> op.createSubcontexts());
    }

    public boolean isEmpty()
    {
        return reg.isEmpty();
    }
}
