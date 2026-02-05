package com.taitl.existential.ops;

import java.util.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.exceptions.NotFoundException;
import com.taitl.existential.helper.*;
import com.taitl.exlogic.existential.*;

/**
 * OpRegistry holds references to Ops, keyed by op name.
 */
public class OpRegistry
{
    protected ExistentialOps ops;
    protected Map<String, OpConfig> reg = new LinkedHashMap<>();

    public OpRegistry(ExistentialOps ops)
    {
        this.ops = ops;
    }

    public OpConfig create(String name)
    {
        Args.cool(name, "name");
        OpConfig o = new OpConfig(name);
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

    public OpConfig get(String id) throws NotFoundException
    {
        Args.cool(id, "id");
        OpConfig o = reg.get(id);
        if (o == null)
        {
            throw new NotFoundException("Context not found, id=" + id);
        }
        return o;
    }

    public OpConfig getcreate(String id)
    {
        Args.cool(id, "id");
        OpConfig o = reg.get(id);
        return (o != null) ? o : create(id);
    }

    public OpConfig remove(String id) throws NotFoundException
    {
        Args.cool(id, "id");
        OpConfig o = reg.get(id);
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
