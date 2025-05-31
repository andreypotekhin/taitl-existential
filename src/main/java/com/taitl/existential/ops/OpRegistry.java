package com.taitl.existential.ops;

import java.util.LinkedHashMap;
import java.util.Map;
import com.taitl.existential.contexts.*;
import com.taitl.existential.exceptions.NotFoundException;
import com.taitl.existential.helper.Args;

/**
 * OpRegistry holds references to OpContexts.
 */
public class OpRegistry
{
    protected Map<String, Op> reg = new LinkedHashMap<>();

    public Op create(String name)
    {
        Args.cool(name, "name");
        Op o = new Op(name);

        for (Context context : Contexts.getcreate(name))
        {
            o.addContext(context);
        }

        synchronized (reg)
        {
            reg.put(name, o);
        }
        return o;
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

    public void createContexts()
    {
        reg.entrySet().stream().forEach(e -> e.getValue().createSubcontexts());
    }

    public boolean isEmpty()
    {
        return reg.isEmpty();
    }
}
