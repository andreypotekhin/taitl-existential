package com.taitl.existential;

import com.taitl.existential.contexts.OpContextRegistry;
import com.taitl.existential.contexts.OpContext;
import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;

public class ExistentialContexts
{
	protected OpContextRegistry registry = new OpContextRegistry();

	public OpContext get(String op)
	{
		Args.cool(op, "op");
		State.verify(!Existential.ex.finalized,
				"Cannot call this method because setup has already been finalized");
		return registry.getcreate(op);
	}

	/**
	 * Called from ExistentialTransactions.begin() to indicate the all
	 * setup/configuration activities, such as setting up validation
	 * rules and event handlers, has been completed.
	 *
	 * From this point, we stop accepting new contexts, custom transactions,
	 * rules and handlers, to be able to freely cache for best performance.
	 */
	public void finalizeSetup()
	{
		if (!Existential.ex.finalized)
		{
			synchronized (Existential.class)
			{
				Existential.ex.finalized = true;

				// Now that we finalized set up of rules and event handlers
				// we'll create custom contexts for all OpContexts that
				// exist context registry. This will result in a call to each
				// and every require(), intent() method of each custom context,
				// and therefore create instances of each Invariants, Intents
				// provided during the setup.
				registry.createContexts();
			}
		}
	}
}
