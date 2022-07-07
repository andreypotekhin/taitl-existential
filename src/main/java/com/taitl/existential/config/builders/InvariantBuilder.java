package com.taitl.existential.config.builders;

import com.taitl.existential.helper.Args;
import com.taitl.existential.invariants.Invariant;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class InvariantBuilder<T> implements RuleSetBuilder<T>
{
    ConfigBuilder parent;
    Invariant<T> target = new Invariant<>();

    public InvariantBuilder(ConfigBuilder parent)
    {
        this.parent = parent;
    }

    public InvariantBuilder<T> create(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.create(condition, description);
        return this;
    }

    public InvariantBuilder<T> change(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.change(condition, description);
        return this;
    }

    public InvariantBuilder<T> delete(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.delete(condition, description);
        return this;
    }

    public InvariantBuilder<T> modify(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.modify(condition, description);
        return this;
    }

    public InvariantBuilder<T> mutate(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.mutate(condition, description);
        return this;
    }

    public InvariantBuilder<T> mutate(BiPredicate<? super T, ? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.mutate(condition, description);
        return this;
    }

    public InvariantBuilder<T> read(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.read(condition, description);
        return this;
    }

    public InvariantBuilder<T> readAndLock(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.readAndLock(condition, description);
        return this;
    }

    public InvariantBuilder<T> write(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.write(condition, description);
        return this;
    }

    public InvariantBuilder<T> transit(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.transit(condition, description);
        return this;
    }

    public InvariantBuilder<T> transit(BiPredicate<? super T, ? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.transit(condition, description);
        return this;
    }

    public InvariantBuilder<T> update(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.update(condition, description);
        return this;
    }

    public InvariantBuilder<T> upsert(Predicate<? super T> condition, String description)
    {
        Args.cool(condition, "condition", description, "description");
        target.upsert(condition, description);
        return this;
    }

    public Invariant build()
    {
        return target;
    }

    public ConfigBuilder done()
    {
        return parent;
    }
}
