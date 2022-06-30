package com.taitl.existential.contexts;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.taitl.existential.Configurable;
import com.taitl.existential.EventSplitter;
import com.taitl.existential.expressions.Expression;
import com.taitl.existential.expressions.Expressions;
import com.taitl.existential.handler.types.EventHandler;
import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;
import com.taitl.existential.instructions.Instructions;
import com.taitl.existential.invariants.Invariants;
import com.taitl.existential.rules.Rule;
import com.taitl.existential.transactions.Transaction;

public class Context implements Configurable
{
    protected static final Supplier<? extends Transaction> DEFAULT_TRANSACTION_FACTORY =
            () -> new Transaction();

    /**
     * Context name, e.g. "/app/flights", "/app/flights/update", "*update"
     */
    protected String name;

    /**
     * Parent context. Rules defined in parent context run prior to
     * rules in child.
     */
    protected Context parent;

    Set<Rule<?>> rules; // Do we need this?

    /**
     * Instructions - event handlers. Includes all event handlers (rules)
     * defined in this context.
     */
    protected Instructions instructions = new Instructions();

    /**
     * Expressions, such as All<T>, defined in this context.
     */
    protected Expressions expressions = new Expressions();

    /**
     * Factory for Transaction.
     */
    protected Set<Supplier<? extends Transaction>> transactionFactories = new LinkedHashSet<>(1);
    {
        transactionFactories.add(DEFAULT_TRANSACTION_FACTORY);
    }

    /**
     * Factory for EventSplitter.
     */
    protected Supplier<? extends EventSplitter> eventSplitterFactory =
            Contexts.eventSplitterFactory;

    // Set<String> initializedFrom = new HashSet<>();

    public Context(String name)
    {
        Args.cool(name, "name");
        this.name = name;
    }

    public Context(String name, Context parent)
    {
        Args.cool(name, "name", parent, "parent");
        this.name = name;
        this.parent = parent;
    }

    /**
     * Associate a custom Transaction with Context.
     *
     * Associating a custom Transaction with Context allows to define
     * rules, such as invariants and intents, for the context using
     * an instance of custom transaction class.
     *
     * Custom transaction may declare its own member fields, thus
     * allowing to carry over information between the rules.
     *
     * Example:
     *   Contexts.get("/app/school")
     *     .transaction(() -> new Transaction(){{
     *        require(new Invariants<Student>() {{
     *             all(student -> student.awake());
     *        }});
     *        require(new Invariants<Teacher>() {{
     *             all(teacher -> teacher.notOnLeave());
     *        }});
     *        intents(new Intents<Student>() {{
     *             read();
     *             write();
     *        }});
     *        intents(new Intents<Teacher>() {{
     *             read();
     *        }});
     *    }})
     *
     *  This method is a multi-entry method which allows creating multiple
     *  transaction factories when called sequentially. The reason to have
     *  multiple transaction factories is to be able to create multiple
     *  custom transactions, for instance, when code similar to the above
     *  appears more than once in different parts of your application (e.g.
     *  this code is split among multiple classes).
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
        // ordinary request processing (e.g. in a controller).
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

    /* Methods for Configurable interface */

    public <T> Context add(EventHandler<T> eh)
    {
        Args.cool(eh, "eh");
        instructions.add(eh);
        return this;
    }

    public <T> Context add(Expression<T> expr)
    {
        Args.cool(expr, "expr");
        expressions.add(expr);
        return this;
    }

    public Context add(Context other)
    {
        Args.cool(other, "other");
        instructions.addAll(other.instructions);
        expressions.addAll(other.expressions);
        return this;
    }

    /**
     * Set up invariants/rules to be enforced for business operation defined by this context.
     *
     * <pre>{@code
     * Contexts.get("/app/flight_school")
     *     .context(() -> new Context(){{
     * 	      require(new Invariants<Pilot>() {{
     *                all((p0, p1) -> p1.hours >= p0.hours, "Flight hours can not go down");
     *                transit((p0, p1) -> p0.flying && !p1.flying, p1.hours += p1.flight().hours);
     * 	      }})
     * 	      require(new Invariants< Cloud>() {{
     *                all(cloud -> cloud.linings.contains(SILVER), "Every cloud has a silver lining");
     * 	      }})
     * }</pre>
     *
     * @param <T> Type parameter
     * @param invariants Invariants (rules) that must be upkept
     */
    public <T> void verify(Invariants<T> invariants)
    {
        Args.cool(invariants, "invariants");
        instructions.addAll(invariants.instructions);
        expressions.addAll(invariants.expressions);
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

    /**
     * Set EventSplitter factory
     *
     * @param supplier EventSplitter supplier
     */
    public void eventSplitter(Supplier<? extends EventSplitter> supplier)
    {
        Args.cool(supplier, "supplier");
        eventSplitterFactory = supplier;
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
}
