package com.taitl.existential.builders;

import java.util.function.*;
import com.taitl.existential.invariants.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Fluent builder for {@link Invariant} rules.
 * Allows describing constraints for specific event types.
 *
 * @param <T> Subject type the invariants target
 */
public class InvariantBuilder<T> implements EvsBuilder<T>
{
    ContextBuilder parent;
    TransactionBuilder parent2;
    Invariant<T> target = new Invariant<>();

    public InvariantBuilder(ContextBuilder parent)
    {
        this.parent = parent;
    }

    public InvariantBuilder(TransactionBuilder parent2)
    {
        this.parent2 = parent2;
    }

    public InvariantBuilder<T> create(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.create(condition, description);
        return this;
    }

    public InvariantBuilder<T> change(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.change(condition, description);
        return this;
    }

    public InvariantBuilder<T> delete(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.delete(condition, description);
        return this;
    }

    public InvariantBuilder<T> modify(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.modify(condition, description);
        return this;
    }

    public InvariantBuilder<T> mutate(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.mutate(condition, description);
        return this;
    }

    public InvariantBuilder<T> mutate(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.mutate(condition, description);
        return this;
    }

    public InvariantBuilder<T> read(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.read(condition, description);
        return this;
    }

    public InvariantBuilder<T> readAndLock(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.readAndLock(condition, description);
        return this;
    }

    public InvariantBuilder<T> write(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.write(condition, description);
        return this;
    }

    public InvariantBuilder<T> transit(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.transit(condition, description);
        return this;
    }

    public InvariantBuilder<T> transit(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.transit(condition, description);
        return this;
    }

    public InvariantBuilder<T> update(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.update(condition, description);
        return this;
    }

    public InvariantBuilder<T> upsert(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.upsert(condition, description);
        return this;
    }

    public Invariant<T> build()
    {
        return target;
    }

    public ContextBuilder done()
    {
        return parent;
    }

    public TransactionBuilder doneTran()
    {
        return parent2;
    }
}
