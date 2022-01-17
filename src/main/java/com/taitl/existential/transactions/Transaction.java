package com.taitl.existential.transactions;

import java.util.UUID;

import javax.naming.Context;

import com.taitl.existential.EventSplitter;
import com.taitl.existential.constants.Strings;
import com.taitl.existential.indexes.Index;

/**
 * Implements a transaction object - a unit of processing within a context.
 * 
 * 'Transactions' in this library are not related to any database transactions, although when integrating, they assumed
 * to be aligned with ones, if you have such. They are simply markers of beginning and end of processing - a business
 * transaction, web request etc.
 * 
 * Transaction's end (a commit) serves as the point where we evaluate expressions, such as All and Exists, existing in
 * the current Context as well as its parent Contexts.
 * 
 * Transaction object helps to store various data in the course of a transaction, and thus makes it possible for one
 * event handler to put some data in and another event handler use it.
 * 
 * TransactionIndexes
 *   When we encounter various events, such as objects creation or mutation, we could want to store some data about it.
 *   For instance, we may wish to create an index on entities, so that Exists<> expression might be evaluated in a
 *   performant way: <code>On<Cat>(c -> location_to_cats.put(c.location, c))</code>
 *
 * TransactionEvents
 *   Different contexts may be responsible for different types of events. To speed up the answer to question 'which events
 *   should be emitted by EventSplitter for this context', the set of relevant events (from the context as well as all its
 *   parents) is created at transaction start and stored in Transaction object. This allows to avoid having to gather such
 *   info for each event from each involved context.
 *
 * Customizing (optional)
 *   To use custom classes for transactions, define them on different context levels, and ask so the system to provide 
 *   an appropriate instance for a business operation using Context.transactionFactory().
 *   For instance, for operation "/app/orders/update":
 *   For data relevant to all transactions, we can be define a custom class AppTransaction class.
 *   An OrdersTransaction class extending AppTransaction can be used for the transactions related to Orders module.
 *   If further customization is needed, one can also define OrdersUpdateTransaction class extending OrdersTransaction.
 *   Set up custom Transaction classes per context:
 *   Contexts.get("/app").transactionFactory(() -> new AppTransaction())
 *   Contexts.get("/app/orders").transactionFactory(() -> new OrdersTransaction())
 *   Contexts.get("/app/orders/update").transactionFactory(() -> new OrdersUpdateTransaction())
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
