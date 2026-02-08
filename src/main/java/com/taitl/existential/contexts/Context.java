package com.taitl.existential.contexts;

import java.util.*;
import java.util.function.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.common.helper.*;
import com.taitl.ex.core.instructions.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.invariants.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class Context implements Configurable
{
    protected static final Supplier<? extends Transaction> DEFAULT_TRANSACTION_FACTORY =
            () -> Creator.create(Transaction.class);

    /**
     * Context name, e.g. "/app/flights", "/app/flights/update", "*update"
     */
    protected String name;

    /**
     * Parent context. Rules defined in parent context run prior to
     * rules in child.
     */
    protected Context parent;

    // TODO: split by stage (execution, validation)
    List<Evs<?>> evs = new ArrayList<>();

    /**
     * Instructions - event handlers. Includes all event handlers (rules)
     * defined in this context.
     */
    protected Instructions instructions = new Instructions();

    /**
     * Expressions, such as All<T>, defined in this context.
     */
    // protected Expressions expressions = new Expressions();

    /**
     * Factory for Transaction.
     */
    protected Set<Supplier<? extends Transaction>> transactionFactories = new LinkedHashSet<>(1);

    {
        transactionFactories.add(DEFAULT_TRANSACTION_FACTORY);
    }

    public Context(String name)
    {
        sane(name, "name");
        this.name = name;
    }

    public Context(String name, Context parent)
    {
        sane(name, "name", parent, "parent");
        this.name = name;
        this.parent = parent;
    }

    /**
     * Set invariants/rules to be enforced for business operation defined by this context.
     *
     * <pre>{@code
     * Ex.contexts().get("/app/flight_school")
     *     .context(() -> new Context(){{
     * 	      invariant(Pilot.class)
     *                .all((p0, p1) -> p1.hours >= p0.hours, "Flight hours can not go down");
     *                .transit((p0, p1) -> p0.flying && !p1.flying, p1.hours += p1.flight().hours);
     * 	      }})
     * }</pre>
     *
     * @param <T> Type parameter
     * @param cls Class for which we define the invariant
     */
    public <T> Invariant<T> invariant(Class<T> cls)
    {
        @SuppressWarnings("unchecked")
        Invariant<T> result = (Invariant<T>) Creator.create(Invariant.class);
        evs.add(result); // BUG: This has no effect
        return result;
    }

    /**
     * Set invariants/rules to be enforced business operation defined by this context.
     *
     * <pre>{@code
     * Ex.contexts().get("/app/flight_school")
     *     .context(() -> new Context(){{
     * 	      invariant(new Invariant<Pilot>() {{
     *                all((p0, p1) -> p1.hours >= p0.hours, "Flight hours can not go down");
     *                transit((p0, p1) -> p0.flying && !p1.flying, p1.hours += p1.flight().hours);
     *          }})
     * }</pre>
     *
     * Warning: the above code implicitly stores a pointer to the enclosing class
     * inside the Invariant object, which may lead to memory leaks. As an alternative,
     * use the {@link #invariant(Class)} method to create an independent Invariant object.
     *
     * @param <T>       Type parameter
     * @param invariant Invariant (rules) that must be upkept
     */
    public <T> void invariant(Invariant<T> invariant)
    {
        sane(invariant, "invariant");
        evs.add(invariant);
        instructions.addAll(invariant.instructions);
        // expressions.addAll(invariant.expressions);
    }

    public <T> Effect<T> effect(Class<T> cls)
    {
        @SuppressWarnings("unchecked")
        Effect<T> result = (Effect<T>) Creator.create(Effect.class);
        evs.add(result); // BUG: This has no effect
        return result;
    }

    /**
     * Set effects for business operation defined by this context.
     *
     * <pre>{@code
     * Ex.contexts().get("/app/flight_school")
     *     .context(() -> new Context(){{
     * 	      effect(new Effect<Pilot>() {{
     *                transit((p0, p1) -> p0.flying && !p1.flying, p1.hours += p1.flight().hours);
     *          }})
     * }</pre>
     *
     * Warning: the above code implicitly stores a pointer to the enclosing class instance
     * inside the Effect object, which may lead to memory leaks. As an alternative,
     * use the {@link #effect(Class)} method to create an independent Effect object.
     *
     * @param <T>       Type parameter
     * @param invariant Invariant (rules) that must be upkept
     */
    public <T> void effect(Effect<T> effect)
    {
        sane(effect, "effect");
        evs.add(effect);
        instructions.addAll(effect.instructions);
        // expressions.addAll(effect.expressions);
    }

    /*
     * Implement Configurable
     */

    public <T> Context add(Evs<T> evs)
    {
        sane(evs, "ev");
        this.evs.add(evs);
        instructions.addAll(evs);
        return this;
    }

    // public <T> Context add(EventHandler<T> eh)
    // {
    // sane(eh, "eh");
    // instructions.add(eh);
    // return this;
    // }
    //
    // public <T> Context add(Expression<T> expr)
    // {
    // sane(expr, "expr");
    // expressions.add(expr);
    // return this;
    // }

    public Context add(Context other)
    {
        sane(other, "other");
        evs.addAll(other.evs);
        instructions.addAll(other.instructions);
        // expressions.addAll(other.expressions);
        return this;
    }

    /**
     * Associate a custom Transaction with Context.
     *
     * Associating a custom Transaction with Context allows to define
     * rules, such as invariants and intents, for the context using
     * an instance of custom transaction class.
     *
     * Custom transaction may declare its own member fields, thus
     * allowing to carry over information between rules/event handlers.
     *
     * Example:
     * Ex.contexts().get("/app/school")
     * .transaction(() -> new Transaction(){{
     * invariant(new Invariant<Student>() {{
     * all(student -> student.awake());
     * }});
     * invariant(new Invariant<Teacher>() {{
     * all(teacher -> teacher.notOnLeave());
     * }});
     * allow(new Intent<Student>() {{
     * read();
     * write();
     * }});
     * allow(new Intent<Teacher>() {{
     * read();
     * }});
     * }})
     *
     * This method is a multi-entry method which allows creating multiple
     * transaction factories when called sequentially. The reason to have
     * multiple transaction factories is to be able to create multiple
     * custom transactions, for instance, when code similar to the above
     * appears more than once in different parts of your application (e.g.
     * this code is split among multiple classes).
     */
    public Context transaction(Supplier<? extends Transaction> supplier)
    {
        if (transactionFactories.size() == 1)
        {
            if (transactionFactories.contains(DEFAULT_TRANSACTION_FACTORY))
            {
                // Replace default transaction factory
                transactionFactories.remove(DEFAULT_TRANSACTION_FACTORY);
            }
        }
        // Guard against multiple calls to .transaction() with same arguments,
        // for instance, if such call exists somewhere in the middle of
        // ordinary request processing (e.g. in a web controller).
        //
        // BUG: This check relies on Supplier's equals() method, which may not work as expected
        // for lambda expressions. In that case, it may be necessary to use a different approach
        // to avoid adding duplicate factories.
        if (!transactionFactories.contains(supplier))
        {
            // TODO: test for different factories to not replace each other
            transactionFactories.add(supplier);
        }
        return this;
    }

    /**
     * Create instances of custom transactions for a Context.
     * This method is called by TransactionRegistry.create().
     *
     * @return List of Transaction objects
     */
    public List<Transaction> createTransactions()
    {
        State.verify(!transactionFactories.isEmpty(), "transactionFactories must not be empty");
        List<Transaction> result = new ArrayList<>(transactionFactories.size());
        for (Supplier<? extends Transaction> supplier : transactionFactories)
        {
            Transaction tr = supplier.get();
            tr.setContext(this);
            result.add(tr);
        }
        return result;
    }

    /* Parent context */

    public boolean hasParent()
    {
        return parent != null;
    }

    public Context getParent()
    {
        return parent;
    }

    public void parent(Context parent)
    {
        this.parent = parent;
    }

    /*
     * public void initializeFromCustomTransaction(Transaction tr) { if
     * (!initializedFromCustomTransaction(tr)) { synchronized (initializedFrom) { if
     * (!initializedFromCustomTransaction(tr)) { instructions.addAll(tr.instructions);
     * expressions.addAll(tr.expressions); initializedFrom.add(tr.getClass().getName()); } } } }
     *
     * public boolean initializedFromCustomTransaction(Transaction tr) { return
     * initializedFrom.contains(tr.getClass().getName()); }
     */

    public String name()
    {
        return name;
    }

    public void name(String name)
    {
        this.name = name;
    }
}
