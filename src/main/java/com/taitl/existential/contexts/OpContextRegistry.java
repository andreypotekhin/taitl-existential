package com.taitl.existential.contexts;

import java.util.LinkedHashMap;
import java.util.Map;
import com.taitl.existential.exceptions.NotFoundException;
import com.taitl.existential.helper.Args;

/**
 * ContextRegistry creates OpContexts and holds references to them
 * throughout application life span (independent of transactions).
 */
public class OpContextRegistry
{
	protected Map<String, OpContext> reg = new LinkedHashMap<>();

	public OpContext create(String op)
	{
		Args.cool(op, "op");
		OpContext o = new OpContext(op);

		for (Context context : Contexts.getcreate(op))
		{
			o.addContext(context);
		}

		synchronized (reg)
		{
			reg.put(op, o);
		}
		return o;
	}

	public OpContext get(String id) throws NotFoundException
	{
		Args.cool(id, "id");
		OpContext o = reg.get(id);
		if (o == null)
		{
			throw new NotFoundException("Context not found, id=" + id);
		}
		return o;
	}

	public OpContext getcreate(String id)
	{
		Args.cool(id, "id");
		OpContext o = reg.get(id);
		return (o != null) ? o : create(id);
	}

	public OpContext remove(String id) throws NotFoundException
	{
		Args.cool(id, "id");
		OpContext o = reg.get(id);
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
}
