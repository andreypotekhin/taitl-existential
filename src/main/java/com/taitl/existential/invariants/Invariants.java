package com.taitl.existential.invariants;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.expressions.All;
import com.taitl.existential.expressions.Exists;
import com.taitl.existential.expressions.Expression;
import com.taitl.existential.expressions.Expressions;
import com.taitl.existential.handler.types.EventHandler;
import com.taitl.existential.handlers.On;
import com.taitl.existential.handlers.OnChange;
import com.taitl.existential.handlers.OnDelete;
import com.taitl.existential.handlers.OnModify;
import com.taitl.existential.handlers.OnMutate;
import com.taitl.existential.handlers.OnRead;
import com.taitl.existential.handlers.OnReadAndLock;
import com.taitl.existential.handlers.OnTransit;
import com.taitl.existential.handlers.OnUpdate;
import com.taitl.existential.handlers.OnUpsert;
import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;
import com.taitl.existential.instructions.Instructions;
import com.taitl.existential.transactions.Transaction;

public class Invariants<T>
{
    /**
     * Parent Transaction object, if any.
     *
     * This field is null for invariants that are not declared on
     * transaction level, e.g. for invariants declared at a context.
     */
    private Transaction tran;

    /**
     * Instructions - event handlers. Includes all event handlers (rules)
     * defined in this context.
     */
    public Instructions instructions = new Instructions();

    /**
     * Expressions, such as All<T>, defined in this context.
     */
    public Expressions expressions = new Expressions();

    /* Event handler methods */

    public Invariants<T> on(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new On<T>(action));
    }

    public Invariants<T> change(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnChange<T>(action));
    }

    public Invariants<T> delete(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnDelete<T>(action));
    }

    public Invariants<T> modify(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnModify<T>(action));
    }

    public Invariants<T> mutate(BiConsumer<? super T, ? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnMutate<T>(action));
    }

    public Invariants<T> read(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnRead<T>(action));
    }

    public Invariants<T> readAndLock(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnReadAndLock<T>(action));
    }

    public Invariants<T> transit(BiConsumer<? super T, ? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnTransit<T>(action));
    }

    public Invariants<T> update(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnUpdate<T>(action));
    }

    public Invariants<T> upsert(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnUpsert<T>(action));
    }

    /* Add event handlers and expressions */

    protected Invariants<T> add(EventHandler<T> eh)
    {
        Args.cool(eh, "eh");
        instructions.add(eh);
        return this;
    }

    public Invariants<T> add(Expression<T> expr)
    {
        Args.cool(expr, "expr");
        expressions.add(expr);
        return this;
    }

    /* Expression methods */

    public Invariants<T> all(Predicate<? super T> predicate)
    {
        Args.cool(predicate, "predicate");
        add(new All<T>(predicate));
        return this;
    }

    public Invariants<T> all(Predicate<? super T> condition, Predicate<? super T> predicate)
    {
        Args.cool(predicate, "predicate");
        add(new All<T>(condition, predicate));
        return this;
    }

    public <V, K> Exists<V, K> exists(String indexName, K key)
    {
        Args.cool(indexName, "indexName", key, "key");
        return new Exists<V, K>(indexName, key);
    }

    public <V, K> Exists<V, K> exists(String indexName, K key, Predicate<Set<V>> predicate)
    {
        Args.cool(indexName, "indexName", key, "key", predicate, "predicate");
        return new Exists<V, K>(indexName, key, predicate);
    }

    public <V, K> Exists<V, K> exists(String indexName, K key,
            BiPredicate<Set<V>, Transaction> bipredicate)
    {
        Args.cool(indexName, "indexName", key, "key", bipredicate, "bipredicate");
        return new Exists<V, K>(indexName, key, bipredicate);
    }

    public <V, K> Exists<V, K> exists(Set<V> values, Predicate<Set<V>> predicate)
    {
        Args.cool(values, "values", predicate, "predicate");
        return new Exists<V, K>(values, predicate, 0);
    }

    public <V, K> Exists<V, K> exists(Set<V> values, BiPredicate<Set<V>, Transaction> bipredicate)
    {
        Args.cool(values, "values", bipredicate, "bipredicate");
        return new Exists<V, K>(values, bipredicate, 0);
    }

    /* Other methods */

    /*
     * public boolean initialized() { return tran != null; }
     */
    public Transaction getTransaction()
    {
        State.cool(tran, "tran");
        return tran;
    }

    public void setTransaction(Transaction tr)
    {
        Args.cool(tr, "tr");
        tran = tr;
    }
}
