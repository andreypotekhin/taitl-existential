package com.taitl.existential.transactions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.taitl.existential.contexts.Context;
import com.taitl.existential.contexts.Contexts;
import com.taitl.existential.exceptions.NotFoundException;
import com.taitl.existential.helper.Args;

/**
 * TransactionRegistry creates OpTrans and holds references to them
 * for the duration of a business transaction.
 */
public class OpTransactionRegistry
{
	protected Map<String, OpTransaction> reg = new LinkedHashMap<>();

	public OpTransaction create(String op)
	{
		Args.cool(op, "op");
		OpTransaction o = new OpTransaction(op, generateId());

		for (Context context : Contexts.getcreate(op))
		{
			for (Transaction tr : context.createTransactions())
			{
				o.addTransaction(tr);
			}
		}

		synchronized (reg)
		{
			reg.put(op, o);
		}
		return o;
	}

	public OpTransaction get(String id) throws NotFoundException
	{
		Args.cool(id, "id");
		OpTransaction o = reg.get(id);
		if (o == null)
		{
			throw new NotFoundException("Transaction not found, id=" + id);
		}
		return o;
	}

	public OpTransaction remove(String id) throws NotFoundException
	{
		Args.cool(id, "id");
		OpTransaction o = reg.get(id);
		if (o == null)
		{
			throw new NotFoundException("Transaction not found, id=" + id);
		}
		synchronized (reg)
		{
			reg.remove(id);
		}
		return o;
	}

	protected UUID generateId()
	{
		return UUID.randomUUID();
	}
}
