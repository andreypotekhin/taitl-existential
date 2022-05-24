package com.taitl.existential.contexts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;
import com.taitl.existential.keys.OpKey;

/**
 * Defines an Operational Context - a set of Context objects relevant
 * to a single business operation.
 *
 * There may be multiple Contexts which apply to a business operation (main
 * context as well as wildcard contexts).
 *
 * For each context, this class may also create one or several custom Context objects,
 * depending on the number of custom Context factories specified in it.
 *
 * This class's job is to hold instances of all these contexts.
 *
 * The Context may declare entity event handlers, which result in side effects,
 * so the order of contexts is important.
 *
 * The order of contexts follows the order of declaration of their corresponding
 * contexts (contexts which are applicable to business operation). Within each context,
 * the order of transactions is same as the order of declaration of its transaction
 * factories.
 */
public class OpContext
{
	/**
	 * Name of business operation, e.g. "/apps/docs/update".
	 */
	String op;

	/**
	 * Context(s) that apply to this operation. This includes main context
	 * (e.g. "/apps/docs/update") as well as any matching wildcard contexts
	 * (e.g. "* /update")
	 */
	List<Context> contexts = new ArrayList<>();

	/**
	 * Main context is the one with name matching op name, e.g. "/apps/docs/update"
	 * All other contexts are wildcard contexts.
	 */
	Context mainContext;

	/**
	 * Instructions - event handlers. Includes all event handlers (rules)
	 * defined in all contexts.
	 * TODO: add ContextRef instruction to each context
	 */
	// protected Instructions instructions = new Instructions();

	/**
	 * Expressions, such as All<T>, defined in all contexts.
	 * TODO: add ContextRef instruction to each context
	 */
	// protected Expressions expressions = new Expressions();

	/**
	 * Factories for Context class.
	 */
	List<Supplier<? extends Context>> contextFactories = new ArrayList<>(1);
	{
		contextFactories.add(() -> new Context("undefined"));
	}

	public OpContext(String op)
	{
		Args.cool(op, "op");
		this.op = op;
	}

	/**
	 * Associate a custom Context class with OpContext.
	 *
	 * Associating a custom Context with OpContext allows to define
	 * rules, such as invariants and intents, for the context using
	 * an instance of a custom context class.
	 *
	 * Example:
	 *   Contexts.get("/app/docs/update")                <-- OpContext
	 *     .context(() -> new Context(){{           <-- Custom context
	 *        require(new Invariants<Document<JSON>>() {{
	 *             write(doc -> doc.verify());
	 *             all(doc -> doc.verified());
	 *        }});
	 *        require(new Invariants<Document<HTML>>() {{
	 *             all(doc -> doc.fullyLoaded());
	 *        }});
	 *        intents(new Intents<Document<JSON>>() {{
	 *             read();
	 *             write();
	 *        }});
	 *        intents(new Intents<Document<HTML>>() {{
	 *             read();
	 *        }});
	 *    }})
	 *
	 *  This method is a multi-entry method which allows creating multiple
	 *  context factories when called sequentially. The reason to have
	 *  multiple context factories is to be able to create multiple
	 *  custom rules, for instance, when code similar to the above
	 *  appears more than once in different parts of your application (e.g.
	 *  when such code is split among multiple classes/components).
	 */
	public OpContext context(Supplier<? extends Context> supplier)
	{
		if (contextFactories.size() == 1)
		{
			// Replace default transaction factory
			contextFactories.set(0, supplier);
		}
		else
		{
			// Remember additional transaction factory
			contextFactories.add(supplier);
		}
		return this;
	}

	public void addContext(Context cont)
	{
		Args.cool(cont, "cont");
		State.verify(!contexts.contains(cont), "This context is already added");
		OpKey opkey = new OpKey(cont.name);
		if (contexts.isEmpty())
		{
			Args.require(!opkey.isWildcard(),
					"First added context is main context, it cannot have wildcard");
			mainContext = cont;
		}
		else
		{
			Args.require(opkey.isWildcard(),
					"No-first added context is may only be a wildcard context");
		}
		contexts.add(cont);
		// instructions.add(new ContextRef(cont));
	}

	/**
	 * For each Context within OpContext, create instances of custom
	 * Contexts using provided context factories, and add them separately
	 * to each Context.add().
	 *
	 * Problem: this duplicates the rules, and therefore, side effects,
	 * which is not desirable!
	 *
	 * This method is called from ExistentialContexts.finalizeSetup(),
	 * one time, when the first transaction begins.
	 */
	public void createSubcontexts()
	{
		State.verify(!contextFactories.isEmpty(), "contextFactories must not be empty");
		State.verify(!contexts.isEmpty(), "Field 'contexts' must not be empty");
		for (Context context : contexts)
		{
			for (Supplier<? extends Context> supplier : contextFactories)
			{
				Context custom = supplier.get();
				context.add(custom);
			}
		}
	}

	/*
	 * public List<Context> createContexts() { State.verify(!contextFactories.isEmpty(),
	 * "contextFactories must not be empty"); List<Context> result = new ArrayList<>(contextFactories.size()); for
	 * (Supplier<? extends Context> supplier : contextFactories) { Context cont = supplier.get(); result.add(cont); }
	 * return result; }
	 */
}
