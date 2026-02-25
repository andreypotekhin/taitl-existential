package com.taitl.existential.builders;

import java.util.function.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Fluent builder for {@link Effect} rules.
 * Provides typed helpers for associating actions with events.
 *
 * @param <T> Subject type the effects target
 */
public class EffectBuilder<T> implements EvsBuilder<T>
{
    ContextBuilder parent;
    TransactionBuilder parent2;
    Effect<T> target;

    public EffectBuilder(ContextBuilder parent)
    {
        sane(parent, "parent");
        throw new IllegalStateException("EffectBuilder requires a TypeKey. Use ContextBuilder.effect(Class<T>) "
                + "or ContextBuilder.effect(TypeKey<T>).");
    }

    public EffectBuilder(ContextBuilder parent, TypeKey<T> typeKey)
    {
        sane(parent, "parent", typeKey, "typeKey");
        this.parent = parent;
        this.target = new Effect<>(typeKey);
    }

    public EffectBuilder(TransactionBuilder parent2)
    {
        sane(parent2, "parent2");
        throw new IllegalStateException("EffectBuilder requires a TypeKey. Use TransactionBuilder.effect(Class<T>) "
                + "or TransactionBuilder.effect(TypeKey<T>).");
    }

    public EffectBuilder(TransactionBuilder parent2, TypeKey<T> typeKey)
    {
        sane(parent2, "parent2", typeKey, "typeKey");
        this.parent2 = parent2;
        this.target = new Effect<>(typeKey);
    }

    public EffectBuilder<T> typeKey(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        target.typeKey(typeKey);
        return this;
    }

    public EffectBuilder<T> create(Consumer<? super T> action)
    {
        sane(action, "action");
        target.create(action);
        return this;
    }

    public EffectBuilder<T> create(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        target.create(action, description);
        return this;
    }

    public EffectBuilder<T> create(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.create(condition, action);
        return this;
    }

    public EffectBuilder<T> create(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.create(condition, action, description);
        return this;
    }

    public EffectBuilder<T> change(Consumer<? super T> action)
    {
        sane(action, "action");
        target.change(action);
        return this;
    }

    public EffectBuilder<T> change(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        target.change(action, description);
        return this;
    }

    public EffectBuilder<T> change(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.change(condition, action);
        return this;
    }

    public EffectBuilder<T> change(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.change(condition, action, description);
        return this;
    }

    public EffectBuilder<T> delete(Consumer<? super T> action)
    {
        sane(action, "action");
        target.delete(action);
        return this;
    }

    public EffectBuilder<T> delete(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        target.delete(action, description);
        return this;
    }

    public EffectBuilder<T> delete(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.delete(condition, action);
        return this;
    }

    public EffectBuilder<T> delete(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.delete(condition, action, description);
        return this;
    }

    public EffectBuilder<T> modify(Consumer<? super T> action)
    {
        sane(action, "action");
        target.modify(action);
        return this;
    }

    public EffectBuilder<T> modify(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        target.modify(action, description);
        return this;
    }

    public EffectBuilder<T> modify(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.modify(condition, action);
        return this;
    }

    public EffectBuilder<T> modify(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.modify(condition, action, description);
        return this;
    }

    public EffectBuilder<T> mutate(BiConsumer<? super T, ? super T> action)
    {
        sane(action, "action");
        target.mutate(action);
        return this;
    }

    public EffectBuilder<T> mutate(BiConsumer<? super T, ? super T> action, String description)
    {
        sane(action, "action", description, "description");
        target.mutate(action, description);
        return this;
    }

    public EffectBuilder<T> mutate(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.mutate(condition, action);
        return this;
    }

    public EffectBuilder<T> mutate(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.mutate(condition, action, description);
        return this;
    }

    public EffectBuilder<T> mutate(BiPredicate<? super T, ? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.mutate(condition, action);
        return this;
    }

    public EffectBuilder<T> mutate(BiPredicate<? super T, ? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.mutate(condition, action, description);
        return this;
    }

    public EffectBuilder<T> read(Consumer<? super T> action)
    {
        sane(action, "action");
        target.read(action);
        return this;
    }

    public EffectBuilder<T> read(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        target.read(action, description);
        return this;
    }

    public EffectBuilder<T> read(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.read(condition, action);
        return this;
    }

    public EffectBuilder<T> read(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.read(condition, action, description);
        return this;
    }

    public EffectBuilder<T> readAndLock(Consumer<? super T> action)
    {
        sane(action, "action");
        target.readAndLock(action);
        return this;
    }

    public EffectBuilder<T> readAndLock(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        target.readAndLock(action, description);
        return this;
    }

    public EffectBuilder<T> readAndLock(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.readAndLock(condition, action);
        return this;
    }

    public EffectBuilder<T> readAndLock(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.readAndLock(condition, action, description);
        return this;
    }

    public EffectBuilder<T> write(Consumer<? super T> action)
    {
        sane(action, "action");
        target.write(action);
        return this;
    }

    public EffectBuilder<T> write(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        target.write(action, description);
        return this;
    }

    public EffectBuilder<T> write(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.write(condition, action);
        return this;
    }

    public EffectBuilder<T> write(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.write(condition, action, description);
        return this;
    }

    public EffectBuilder<T> transit(BiConsumer<? super T, ? super T> action)
    {
        sane(action, "action");
        target.transit(action);
        return this;
    }

    public EffectBuilder<T> transit(BiConsumer<? super T, ? super T> action, String description)
    {
        sane(action, "action", description, "description");
        target.transit(action, description);
        return this;
    }

    public EffectBuilder<T> transit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.transit(condition, action);
        return this;
    }

    public EffectBuilder<T> transit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.transit(condition, action, description);
        return this;
    }

    public EffectBuilder<T> transit(BiPredicate<? super T, ? super T> condition,
            BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.transit(condition, action);
        return this;
    }

    public EffectBuilder<T> transit(BiPredicate<? super T, ? super T> condition,
            BiConsumer<? super T, ? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.transit(condition, action, description);
        return this;
    }

    public EffectBuilder<T> update(Consumer<? super T> action)
    {
        sane(action, "action");
        target.update(action);
        return this;
    }

    public EffectBuilder<T> update(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        target.update(action, description);
        return this;
    }

    public EffectBuilder<T> update(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.update(condition, action);
        return this;
    }

    public EffectBuilder<T> update(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.update(condition, action, description);
        return this;
    }

    public EffectBuilder<T> upsert(Consumer<? super T> action)
    {
        sane(action, "action");
        target.upsert(action);
        return this;
    }

    public EffectBuilder<T> upsert(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        target.upsert(action, description);
        return this;
    }

    public EffectBuilder<T> upsert(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        target.upsert(condition, action);
        return this;
    }

    public EffectBuilder<T> upsert(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        target.upsert(condition, action, description);
        return this;
    }

    public Effect<T> build()
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
