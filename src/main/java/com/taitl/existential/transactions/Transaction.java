package com.taitl.existential.transactions;

import java.util.UUID;

import javax.naming.Context;

import com.taitl.existential.EventSplitter;
import com.taitl.existential.constants.Strings;
import com.taitl.existential.indexes.Index;

/**
 * Implements a transaction object - a unit of processing within a context.
 * <p>
 * 'Transactions' in this library are not related to database transactions, although when integrating, they assumed
 * to be aligned with ones, if you have such. They are simply markers of beginning and end of processing - of a 
 * business transaction, web request etc.
 * <p>
 * Transaction's end (a commit) serves as the point where we evaluate expressions, such as All and Exists, defined in
 * the current Context as well as its parent Contexts.
 * <p>
 * Transaction object helps to store various data, so it is possible to pass data from one event handler to another.
 * <p>
 * TransactionIndexes<p>
 *   When we encounter various events, such as objects creation or mutation, we could want to store some data about it.
 *   For instance, we may wish to create an index on entities, so that Exists<> expression might be evaluated in a
 *   performant way: {@code On<Cat>((c, tr) -> tr.index("location_to_cats").put(c.location, c))}
 * <p>
 * TransactionEvents<p>
 *   Different contexts may be interested in different types of events. To speed up the answer to question 'which events
 *   should be emitted by EventSplitter for this context', the set of relevant event types (from the context as well as all its
 *   parents) is created at transaction start and stored in Transaction object. This allows to avoid having to gather such
 *   info for each event from each involved context.
 * <p>
 * Customizing<p>
 *   To use custom classes for transactions, define them on different context levels, and ask the system to provide 
 *   an appropriate instance for a business operation using {@code Context.transactionFactory()}.<br>
 *   For instance, for operation "/app/orders/update":<br>
 *   For data relevant to all transactions, define a custom {@code AppTransaction } class.<br>
 *   An {@code OrdersTransaction} class extending {@code AppTransaction } can be used for the transactions related to Orders module.<br>
 *   If further customization is needed, one can also define {@code OrdersUpdateTransaction} class extending {@code OrdersTransaction}.<p>
 *   Set up custom Transaction classes per context:<br>
 *   <pre>{@code
 *   Contexts.get("/app").transactionFactory(() -> new AppTransaction())
 *   Contexts.get("/app/orders").transactionFactory(() -> new OrdersTransaction())
 *   Contexts.get("/app/orders/update").transactionFactory(() -> new OrdersUpdateTransaction())
 *   }</pre>
 *   If custom transaction class is not defined for a context, the transaction class from its parent context will be used.
 * 
 * @author Andrey Potekhin
 * @see Context
 * @see TransactionIndexes
 * @see TransactionEvents
 * @see EventSplitter
 */
public class Transaction
{
    public final UUID id;
    public final String op;

    public TransactionIndexes indexes = new TransactionIndexes(this);
    public TransactionEvents events = new TransactionEvents(this);

    public Transaction(String op)
    {
        if (op == null)
        {
            throw new IllegalArgumentException(Strings.ARG_OP);
        }
        this.op = op;
        this.id = generateId();
    }

    public <K, V> Index<K, V> index(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException(Strings.ARG_NAME);
        }
        return indexes.get(name);
    }

    protected UUID generateId()
    {
        return UUID.randomUUID();
    }
}
