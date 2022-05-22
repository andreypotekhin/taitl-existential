package com.taitl.existential.invariants;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.expressions.All;
import com.taitl.existential.expressions.Exists;
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
import com.taitl.existential.transactions.Transaction;

public class Invariants<T>
{
    private Transaction tran;

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

    protected Invariants<T> add(EventHandler<T> eh)
    {
        State.cool(tran, "tran");
        tran.add(eh);
        return this;
    }

    /* Expression methods */

    public Invariants<T> all(Predicate<? super T> predicate)
    {
        Args.cool(predicate, "predicate");
        tran.add(new All<T>(predicate));
        return this;
    }

    public Invariants<T> all(Predicate<? super T> condition, Predicate<? super T> predicate)
    {
        Args.cool(predicate, "predicate");
        tran.add(new All<T>(condition, predicate));
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

    public void setTransaction(Transaction tr)
    {
        Args.cool(tr, "tr");
        tran = tr;
    }
}
