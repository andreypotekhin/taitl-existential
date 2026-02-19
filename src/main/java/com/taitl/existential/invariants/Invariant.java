package com.taitl.existential.invariants;

import java.util.*;
import java.util.function.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.access_handlers.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.quantifiers.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Declares invariants over entities within a transaction or a specific context.
 * Invariants are assembled from event handlers and quantifiers such as {@link All}
 * and {@link Exists}, and are evaluated when a transaction is validated.
 *
 * @param <T> entity type the invariant applies to
 */
public class Invariant<T> implements Evs<T>, Constraints<T>
{
    /**
     * Parent Transaction, if any.
     * This field is null for invariants that are not declared at the
     * transaction level, e.g. invariants declared in a Context.
     */
    Transaction tran;

    /**
     * Entity event handlers.
     */
    List<Ev<T>> evs = new ArrayList<>();

    /* Event handler methods */

    public Invariant<T> on(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new On<T>(condition, null, description));
    }

    public Invariant<T> create(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnCreate<T>(condition, null, description));
    }

    public Invariant<T> change(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnChange<T>(condition, null, description));
    }

    public Invariant<T> delete(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnDelete<T>(condition, null, description));
    }

    public Invariant<T> modify(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnModify<T>(condition, null, description));
    }

    public Invariant<T> mutate(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnMutate<T>(condition, null, description));
    }

    public Invariant<T> mutate(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnMutate<T>(condition, null, description));
    }

    public Invariant<T> read(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnRead<T>(condition, null, description));
    }

    public Invariant<T> readAndLock(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnReadAndLock<T>(condition, null, description));
    }

    public Invariant<T> write(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnWrite<T>(condition, null, description));
    }

    public Invariant<T> transit(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnTransit<T>(condition, null, description));
    }

    public Invariant<T> transit(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnTransit<T>(condition, null, description));
    }

    public Invariant<T> update(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnUpdate<T>(condition, null, description));
    }

    public Invariant<T> upsert(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnUpsert<T>(condition, null, description));
    }

    /* Expression methods */

    public Invariant<T> all(Predicate<? super T> predicate, String description)
    {
        sane(predicate, "predicate");
        add(new All<T>(predicate, description));
        return this;
    }

    public Invariant<T> all(Predicate<? super T> condition, Predicate<? super T> predicate, String description)
    {
        sane(condition, "condition", predicate, "predicate", description, "description");
        add(new All<T>(condition, predicate, description));
        return this;
    }

    public <T> Exists<T> exists(Collection<T> values, Predicate<T> predicate)
    {
        sane(values, "values", predicate, "predicate");
        return new Exists<T>(values, predicate);
    }

    public <T> Exists<T> exists(Collection<T> values, BiPredicate<T, Transaction> bipredicate)
    {
        sane(values, "values", bipredicate, "bipredicate");
        return new Exists<T>(values, bipredicate);
    }

    public <T> Exists<T> exists(Collection<T> values, Predicate<Collection<T>> predicate, int placeholder)
    {
        sane(values, "values", predicate, "predicate");
        return new Exists<T>(values, predicate, placeholder);
    }

    public <T> Exists<T> exists(Collection<T> values, BiPredicate<Collection<T>, Transaction> bipredicate,
            int placeholder)
    {
        sane(values, "values", bipredicate, "bipredicate");
        return new Exists<T>(values, bipredicate, placeholder);
    }

    /* Evs implementation */

    public Invariant<T> add(Ev<T> ev)
    {
        sane(ev, "eh");
        evs.add(ev);
        return this;
    }

    public List<Ev<T>> list()
    {
        return evs;
    }

    /* Attributes */

    public Transaction transaction()
    {
        cool(tran, "tran");
        return tran;
    }

    public void transaction(Transaction tr)
    {
        sane(tr, "tr");
        tran = tr;
    }

}
