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
import com.taitl.existential.handlers.*;
import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;
import com.taitl.existential.instructions.Instructions;
import com.taitl.existential.rules.RuleSet;
import com.taitl.existential.transactions.Transaction;

public class Invariant<T> implements RuleSet<T>
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

    public Invariant<T> on(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new On<T>(condition, null, description));
    }

    public Invariant<T> create(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        return add(new OnCreate<T>(condition, null, description));
    }

    public Invariant<T> change(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new OnChange<T>(condition, null, description));
    }

    public Invariant<T> delete(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new OnDelete<T>(condition, null, description));
    }

    public Invariant<T> modify(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new OnModify<T>(condition, null, description));
    }

    public Invariant<T> mutate(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new OnMutate<T>(condition, null, description));
    }

    public Invariant<T> mutate(BiPredicate<? super T, ? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new OnMutate<T>(condition, null, description));
    }

    public Invariant<T> read(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new OnRead<T>(condition, null, description));
    }

    public Invariant<T> readAndLock(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new OnReadAndLock<T>(condition, null, description));
    }

    public Invariant<T> write(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new OnWrite<T>(condition, null, description));
    }

    public Invariant<T> transit(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new OnTransit<T>(condition, null, description));
    }

    public Invariant<T> transit(BiPredicate<? super T, ? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new OnTransit<T>(condition, null, description));
    }

    public Invariant<T> update(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new OnUpdate<T>(condition, null, description));
    }

    public Invariant<T> upsert(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition");
        return add(new OnUpsert<T>(condition, null, description));
    }

    /* Add event handlers and expressions */

    protected Invariant<T> add(EventHandler<T> eh)
    {
        Args.cool(eh, "eh");
        instructions.add(eh);
        return this;
    }

    public Invariant<T> add(Expression<T> expr)
    {
        Args.cool(expr, "expr");
        expressions.add(expr);
        return this;
    }

    /* Expression methods */

    public Invariant<T> all(Predicate<? super T> predicate, String description)
    {
        Args.cool(predicate, "predicate");
        add(new All<T>(predicate, description));
        return this;
    }

    public Invariant<T> all(Predicate<? super T> condition, Predicate<? super T> predicate, String description)
    {
        Args.cool(condition, "condition", predicate, "predicate", description, "description");
        add(new All<T>(condition, predicate, description));
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
