package com.taitl.existential.builders;

import com.taitl.existential.helper.Args;
import com.taitl.existential.invariants.Effect;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EffectBuilder<T> implements RuleSetBuilder<T>
{
    ConfigBuilder parent;
    Effect<T> target = new Effect<>();

    public EffectBuilder(ConfigBuilder parent)
    {
        this.parent = parent;
    }

    public EffectBuilder<T> create(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        target.create(action);
        return this;
    }

    public EffectBuilder<T> create(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        target.create(action, description);
        return this;
    }

    public EffectBuilder<T> create(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.create(condition, action);
        return this;
    }

    public EffectBuilder<T> create(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.create(condition, action, description);
        return this;
    }

    public EffectBuilder<T> change(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        target.change(action);
        return this;
    }

    public EffectBuilder<T> change(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        target.change(action, description);
        return this;
    }

    public EffectBuilder<T> change(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.change(condition, action);
        return this;
    }

    public EffectBuilder<T> change(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.change(condition, action, description);
        return this;
    }

    public EffectBuilder<T> delete(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        target.delete(action);
        return this;
    }

    public EffectBuilder<T> delete(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        target.delete(action, description);
        return this;
    }

    public EffectBuilder<T> delete(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.delete(condition, action);
        return this;
    }

    public EffectBuilder<T> delete(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.delete(condition, action, description);
        return this;
    }

    public EffectBuilder<T> modify(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        target.modify(action);
        return this;
    }

    public EffectBuilder<T> modify(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        target.modify(action, description);
        return this;
    }

    public EffectBuilder<T> modify(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.modify(condition, action);
        return this;
    }

    public EffectBuilder<T> modify(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.modify(condition, action, description);
        return this;
    }

    public EffectBuilder<T> mutate(BiConsumer<? super T, ? super T> action)
    {
        Args.cool(action, "action");
        target.mutate(action);
        return this;
    }

    public EffectBuilder<T> mutate(BiConsumer<? super T, ? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        target.mutate(action, description);
        return this;
    }

    public EffectBuilder<T> mutate(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.mutate(condition, action);
        return this;
    }

    public EffectBuilder<T> mutate(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.mutate(condition, action, description);
        return this;
    }

    public EffectBuilder<T> mutate(BiPredicate<? super T, ? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.mutate(condition, action);
        return this;
    }

    public EffectBuilder<T> mutate(BiPredicate<? super T, ? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.mutate(condition, action, description);
        return this;
    }

    public EffectBuilder<T> read(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        target.read(action);
        return this;
    }

    public EffectBuilder<T> read(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        target.read(action, description);
        return this;
    }

    public EffectBuilder<T> read(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.read(condition, action);
        return this;
    }

    public EffectBuilder<T> read(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.read(condition, action, description);
        return this;
    }

    public EffectBuilder<T> readAndLock(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        target.readAndLock(action);
        return this;
    }

    public EffectBuilder<T> readAndLock(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        target.readAndLock(action, description);
        return this;
    }

    public EffectBuilder<T> readAndLock(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.readAndLock(condition, action);
        return this;
    }

    public EffectBuilder<T> readAndLock(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.readAndLock(condition, action, description);
        return this;
    }

    public EffectBuilder<T> write(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        target.write(action);
        return this;
    }

    public EffectBuilder<T> write(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        target.write(action, description);
        return this;
    }

    public EffectBuilder<T> write(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.write(condition, action);
        return this;
    }

    public EffectBuilder<T> write(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.write(condition, action, description);
        return this;
    }

    public EffectBuilder<T> transit(BiConsumer<? super T, ? super T> action)
    {
        Args.cool(action, "action");
        target.transit(action);
        return this;
    }

    public EffectBuilder<T> transit(BiConsumer<? super T, ? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        target.transit(action, description);
        return this;
    }

    public EffectBuilder<T> transit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.transit(condition, action);
        return this;
    }

    public EffectBuilder<T> transit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.transit(condition, action, description);
        return this;
    }

    public EffectBuilder<T> transit(BiPredicate<? super T, ? super T> condition,
            BiConsumer<? super T, ? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.transit(condition, action);
        return this;
    }

    public EffectBuilder<T> transit(BiPredicate<? super T, ? super T> condition,
            BiConsumer<? super T, ? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.transit(condition, action, description);
        return this;
    }

    public EffectBuilder<T> update(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        target.update(action);
        return this;
    }

    public EffectBuilder<T> update(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        target.update(action, description);
        return this;
    }

    public EffectBuilder<T> update(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.update(condition, action);
        return this;
    }

    public EffectBuilder<T> update(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.update(condition, action, description);
        return this;
    }

    public EffectBuilder<T> upsert(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        target.upsert(action);
        return this;
    }

    public EffectBuilder<T> upsert(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        target.upsert(action, description);
        return this;
    }

    public EffectBuilder<T> upsert(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        target.upsert(condition, action);
        return this;
    }

    public EffectBuilder<T> upsert(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        target.upsert(condition, action, description);
        return this;
    }

    public Effect<T> build()
    {
        return target;
    }

    public ConfigBuilder done()
    {
        return parent;
    }
}
