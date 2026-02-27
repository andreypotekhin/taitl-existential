package com.taitl.existential.configs;

import com.taitl.ex.common.creator.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.invariants.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Describes a business context within a configuration, including invariants,
 * effects, and event handlers that should apply to a specific operation.
 *
 * Contexts can form a hierarchy, where parent rules execute before child rules,
 * and can be customized with transaction and event-splitting factories.
 *
 * @see Config
 * @see Transaction
 */
// TODO: add context() method for adding child contexts
public class Context implements Configurable, Evaluable
{
    public static Supplier<? extends Context> FACTORY = () -> Creator.create(Context.class);

    /**
     * Context name, e.g. "/app/flights", "/app/flights/update", "*update"
     */
    protected String name;

    /**
     * Parent context. Rules defined in parent context run prior to
     * rules in child.
     */
    protected Context parent;

    /**
     * Configured rules: invariants, effects, access rules
     */
    // TODO: split by stage (execution, validation)
    List<Evs<?>> evs = new ArrayList<>();

    /**
     * Instructions - event handlers. Includes all event handlers (rules)
     * defined in this context.
     */
    // protected Instructions instructions = new Instructions();

    /** Transaction factory */
    protected Supplier<? extends Transaction> transactionFactory = Transaction.FACTORY;

    /**
     * Creates a context with the provided operation name.
     *
     * @param name Operation name or context path
     */
    public Context(String name)
    {
        sane(name, "name");
        this.name = name;
    }

    /**
     * Creates a child context with the provided name and parent context.
     *
     * @param name   Operation name or context path
     * @param parent Parent context
     */
    public Context(String name, Context parent)
    {
        sane(name, "name", parent, "parent");
        this.name = name;
        this.parent = parent;
        this.transactionFactory = null;
    }

    /*
     * Configure invariants, effects, access rules
     */

    /**
     * Sets invariants (rules) enforced for the business operation defined by this context.
     *
     * <pre>{@code
     * Ex.contexts("/app/flight_school")
     *     .context(() -> new Context() {{
     *         invariant(new Invariant<Pilot>() {{
     *             all((p0, p1) -> p1.hours >= p0.hours, "Flight hours cannot go down");
     *             transit((p0, p1) -> p0.flying && !p1.flying, p1.hours += p1.flight().hours);
     *         }});
     *     }});
     * }</pre>
     *
     * @param <T>       Type parameter
     * @param invariant Invariant (rules) that must be upheld
     */
    public <T> void invariant(Invariant<T> invariant)
    {
        sane(invariant, "invariant");
        add(invariant);
    }

    /**
     * Sets effects for the business operation defined by this context.
     *
     * <pre>{@code
     * Ex.contexts("/app/flight_school")
     *     .context(() -> new Context() {{
     *         effect(new Effect<Pilot>() {{
     *             transit((p0, p1) -> p0.flying && !p1.flying, p1.hours += p1.flight().hours);
     *         }});
     *     }});
     * }</pre>
     *
     * @param <T>       Type parameter
     * @param effect    Effect to register
     */
    public <T> void effect(Effect<T> effect)
    {
        sane(effect, "effect");
        add(effect);
    }

    /**
     * Associates a custom Transaction with this Context.
     * The rules defined for a Transaction do not change the Context,
     * but they are evaluated alongside it.
     * A custom transaction may declare its own fields, allowing you to
     * carry information between rules and event handlers.
     * Adding a Transaction can happen at any time, for instance in a
     * web endpoint handler or other business method, which allows it
     * to access local scope variables and the current instance in its rules.
     *
     * Example:
     * <pre>{@code
     * Ex.contexts("/app/school")
     *      .transaction(() -> new Transaction() {{
     *          invariant(new Invariant<Student>() {{
     *              all(student -> student.awake());
     *          }});
     *          invariant(new Invariant<Teacher>() {{
     *              all(teacher -> teacher.notOnLeave());
     *          }});
     *          allow(new Intent<Student>() {{
     *              read();
     *              write();
     *          }});
     *          allow(new Intent<Teacher>() {{
     *              read();
     *          }});
     *      }});
     * }</pre>
     */
    public Context transaction(Supplier<? extends Transaction> supplier)
    {
        sane(supplier, "supplier");
        transactionFactory = supplier;
        return this;
    }

    /*
     * Configurable interface
     */

    /**
     * Adds a rule set to this context and registers its handlers.
     *
     * @param evs Rule set to add
     * @param <T> Entity type
     */
    public <T> void add(Evs<T> evs)
    {
        sane(evs, "ev");
        this.evs.add(evs);
        // instructions.addAll(evs);
    }

    /**
     * Merges all rules and instructions from another context into this one.
     *
     * @param other Context to merge from
     * @return This context for chaining
     */
    public Context addAll(Context other)
    {
        sane(other, "other");
        evs.addAll(other.evs);
        // instructions.addAll(other.instructions);
        return this;
    }

    public List<Evs<?>> evs()
    {
        return evs;
    }

    /*
     * Attributes
     */

    /**
     * Returns true if this context has a parent context.
     *
     * @return Whether a parent context is set
     */
    public boolean hasParent()
    {
        return parent != null;
    }

    /**
     * Returns the parent context, if any.
     *
     * @return Parent context or null
     */
    public Context parent()
    {
        return parent;
    }

    /**
     * Sets the parent context.
     *
     * @param parent Parent context
     */
    public void parent(Context parent)
    {
        this.parent = parent;
    }

    /**
     * Returns the context name/path.
     *
     * @return Context name
     */
    public String name()
    {
        return name;
    }

    /**
     * Sets the operation name for this context.
     *
     * @param op Operation name
     */
    public void op(String op)
    {
        this.name = op;
    }

    /**
     * Returns the transaction factory used for this context,
     * inheriting from the parent when not set.
     *
     * @return Transaction factory
     */
    public Supplier<? extends Transaction> transactionFactory()
    {
        if (transactionFactory != null)
        {
            return transactionFactory;
        }
        return parent != null ? parent.transactionFactory() : Transaction.FACTORY;
    }
}
