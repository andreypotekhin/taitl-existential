package com.taitl.existential.builders;

import com.taitl.ex.common.creator.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.keys.*;

import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Builds {@link Effect} rules.
 *
 * @param <T> Type to apply the effects
 */
public class EffectBuilder<T> implements EvsBuilder<T>
{
    ContextBuilder parent;
    TransactionBuilder parent2;
    Effect<T> target;

    /**
     * Creates an effect builder for a context or transaction.
     *
     * @param parent
     *            Parent builder that owns this configuration.
     */
    public EffectBuilder(ContextBuilder parent)
    {
        sane(parent, "parent");
        throw new IllegalStateException("EffectBuilder requires a TypeKey. Use ContextBuilder.effect(Class<T>) "
                + "or ContextBuilder.effect(TypeKey<T>).");
    }

    /**
     * Creates an effect builder for a context or transaction.
     *
     * @param parent
     *            Parent builder that owns this configuration.
     * @param typeKey
     *            Type key identifying the rule subject.
     */
    public EffectBuilder(ContextBuilder parent, TypeKey<T> typeKey)
    {
        sane(parent, "parent", typeKey, "typeKey");
        this.parent = parent;
        this.target = Creator.create(Effect.class, new Class<?>[] { TypeKey.class }, typeKey);
    }

    /**
     * Creates an effect builder for a context or transaction.
     *
     * @param parent2
     *            Parent builder that owns this configuration.
     */
    public EffectBuilder(TransactionBuilder parent2)
    {
        sane(parent2, "parent2");
        throw new IllegalStateException("EffectBuilder requires a TypeKey. Use TransactionBuilder.effect(Class<T>) "
                + "or TransactionBuilder.effect(TypeKey<T>).");
    }

    /**
     * Creates an effect builder for a context or transaction.
     *
     * @param parent2
     *            Parent builder that owns this configuration.
     * @param typeKey
     *            Type key identifying the rule subject.
     */
    public EffectBuilder(TransactionBuilder parent2, TypeKey<T> typeKey)
    {
        sane(parent2, "parent2", typeKey, "typeKey");
        this.parent2 = parent2;
        this.target = Creator.create(Effect.class, new Class<?>[] { TypeKey.class }, typeKey);
    }

    /**
     * Sets the type key for this rule.
     *
     * @param typeKey
     *            Type key identifying the rule subject.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> typeKey(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        checkBuilder();
        target.typeKey(typeKey);
        return this;
    }

    /**
     * Registers an effect for create events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> create(Consumer<? super T> action)
    {
        sane(action, "action");
        checkBuilder();
        target.create(action);
        return this;
    }

    /**
     * Registers an effect for create events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> create(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        checkBuilder();
        target.create(action, description);
        return this;
    }

    /**
     * Registers an effect for create events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> create(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.create(condition, action);
        return this;
    }

    /**
     * Registers an effect for create events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> create(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.create(condition, action, description);
        return this;
    }

    /**
     * Registers an effect for delete events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> delete(Consumer<? super T> action)
    {
        sane(action, "action");
        checkBuilder();
        target.delete(action);
        return this;
    }

    /**
     * Registers an effect for delete events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> delete(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        checkBuilder();
        target.delete(action, description);
        return this;
    }

    /**
     * Registers an effect for delete events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> delete(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.delete(condition, action);
        return this;
    }

    /**
     * Registers an effect for delete events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> delete(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.delete(condition, action, description);
        return this;
    }

    /**
     * Registers an effect for transit events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> transit(BiConsumer<? super T, ? super T> action)
    {
        sane(action, "action");
        checkBuilder();
        target.transit(action);
        return this;
    }

    /**
     * Registers an effect for transit events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> transit(BiConsumer<? super T, ? super T> action, String description)
    {
        sane(action, "action", description, "description");
        checkBuilder();
        target.transit(action, description);
        return this;
    }

    /**
     * Registers an effect for transit events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> transit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.transit(condition, action);
        return this;
    }

    /**
     * Registers an effect for transit events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> transit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.transit(condition, action, description);
        return this;
    }

    /**
     * Registers an effect for transit events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> transit(BiPredicate<? super T, ? super T> condition,
            BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.transit(condition, action);
        return this;
    }

    /**
     * Registers an effect for transit events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> transit(BiPredicate<? super T, ? super T> condition,
            BiConsumer<? super T, ? super T> action,
            String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.transit(condition, action, description);
        return this;
    }

    /**
     * Registers an effect for read events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> read(Consumer<? super T> action)
    {
        sane(action, "action");
        checkBuilder();
        target.read(action);
        return this;
    }

    /**
     * Registers an effect for read events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> read(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        checkBuilder();
        target.read(action, description);
        return this;
    }

    /**
     * Registers an effect for read events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> read(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.read(condition, action);
        return this;
    }

    /**
     * Registers an effect for read events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> read(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.read(condition, action, description);
        return this;
    }

    /**
     * Registers an effect for rl events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> rl(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        checkBuilder();
        target.rl(action, description);
        return this;
    }

    /**
     * Registers an effect for rl events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> rl(Consumer<? super T> action)
    {
        sane(action, "action");
        checkBuilder();
        target.rl(action);
        return this;
    }

    /**
     * Registers an effect for rl events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> rl(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.rl(condition, action);
        return this;
    }

    /**
     * Registers an effect for rl events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> rl(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.rl(condition, action, description);
        return this;
    }

    /**
     * Registers an effect for write events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> write(Consumer<? super T> action)
    {
        sane(action, "action");
        checkBuilder();
        target.write(action);
        return this;
    }

    /**
     * Registers an effect for write events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> write(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        checkBuilder();
        target.write(action, description);
        return this;
    }

    /**
     * Registers an effect for write events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> write(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.write(condition, action);
        return this;
    }

    /**
     * Registers an effect for write events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> write(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.write(condition, action, description);
        return this;
    }

    /**
     * Registers an effect for port events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> port(BiConsumer<? super T, ? super T> action)
    {
        sane(action, "action");
        checkBuilder();
        target.port(action);
        return this;
    }

    /**
     * Registers an effect for port events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> port(BiConsumer<? super T, ? super T> action, String description)
    {
        sane(action, "action", description, "description");
        checkBuilder();
        target.port(action, description);
        return this;
    }

    /**
     * Registers an effect for port events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> port(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.port(condition, action);
        return this;
    }

    /**
     * Registers an effect for port events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> port(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.port(condition, action, description);
        return this;
    }

    /**
     * Registers an effect for port events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> port(BiPredicate<? super T, ? super T> condition,
            BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.port(condition, action);
        return this;
    }

    /**
     * Registers an effect for port events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> port(BiPredicate<? super T, ? super T> condition,
            BiConsumer<? super T, ? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.port(condition, action, description);
        return this;
    }

    /**
     * Registers an effect for update events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> update(Consumer<? super T> action)
    {
        sane(action, "action");
        checkBuilder();
        target.update(action);
        return this;
    }

    /**
     * Registers an effect for update events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> update(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        checkBuilder();
        target.update(action, description);
        return this;
    }

    /**
     * Registers an effect for update events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> update(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.update(condition, action);
        return this;
    }

    /**
     * Registers an effect for update events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> update(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.update(condition, action, description);
        return this;
    }

    /**
     * Registers an effect for cu events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> cu(Consumer<? super T> action)
    {
        sane(action, "action");
        checkBuilder();
        target.cu(action);
        return this;
    }

    /**
     * Registers an effect for cu events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> cu(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        checkBuilder();
        target.cu(action, description);
        return this;
    }

    /**
     * Registers an effect for cu events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> cu(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.cu(condition, action);
        return this;
    }

    /**
     * Registers an effect for cu events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> cu(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.cu(condition, action, description);
        return this;
    }

    /**
     * Registers an effect for cud events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> cud(Consumer<? super T> action)
    {
        sane(action, "action");
        checkBuilder();
        target.cud(action);
        return this;
    }

    /**
     * Registers an effect for cud events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> cud(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        checkBuilder();
        target.cud(action, description);
        return this;
    }

    /**
     * Registers an effect for cud events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> cud(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.cud(condition, action);
        return this;
    }

    /**
     * Registers an effect for cud events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> cud(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.cud(condition, action, description);
        return this;
    }

    /**
     * Registers an effect for ud events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> ud(Consumer<? super T> action)
    {
        sane(action, "action");
        checkBuilder();
        target.ud(action);
        return this;
    }

    /**
     * Registers an effect for ud events.
     *
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> ud(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        checkBuilder();
        target.ud(action, description);
        return this;
    }

    /**
     * Registers an effect for ud events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> ud(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        checkBuilder();
        target.ud(condition, action);
        return this;
    }

    /**
     * Registers an effect for ud events.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param action
     *            Action to run when the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public EffectBuilder<T> ud(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        checkBuilder();
        target.ud(condition, action, description);
        return this;
    }

    /**
     * Builds the configured effect.
     * @return
     *            The configured rule.
     */
    public Effect<T> build()
    {
        return target;
    }

    /**
     * Starts or adds an invariant rule in this builder.
     *
     * @param cls
     *            Class of the rule subject.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> InvariantBuilder<U> invariant(Class<U> cls)
    {
        sane(cls, "cls");
        if (parent != null)
        {
            return parent.invariant(cls);
        }
        return parent2.invariant(cls);
    }

    /**
     * Starts or adds an invariant rule in this builder.
     *
     * @param typeKey
     *            Type key identifying the rule subject.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> InvariantBuilder<U> invariant(TypeKey<U> typeKey)
    {
        sane(typeKey, "typeKey");
        if (parent != null)
        {
            return parent.invariant(typeKey);
        }
        return parent2.invariant(typeKey);
    }

    /**
     * Starts or adds an invariant rule in this builder.
     *
     * @param invariant
     *            Invariant to add or configure.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> ContextBuilder invariant(Invariant<U> invariant)
    {
        sane(invariant, "invariant");
        return parentContext().invariant(invariant);
    }

    /**
     * Starts or adds an effect rule in this builder.
     *
     * @param cls
     *            Class of the rule subject.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> EffectBuilder<U> effect(Class<U> cls)
    {
        sane(cls, "cls");
        if (parent != null)
        {
            return parent.effect(cls);
        }
        return parent2.effect(cls);
    }

    /**
     * Starts or adds an effect rule in this builder.
     *
     * @param typeKey
     *            Type key identifying the rule subject.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> EffectBuilder<U> effect(TypeKey<U> typeKey)
    {
        sane(typeKey, "typeKey");
        if (parent != null)
        {
            return parent.effect(typeKey);
        }
        return parent2.effect(typeKey);
    }

    /**
     * Starts or adds an effect rule in this builder.
     *
     * @param effect
     *            Effect to add or configure.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> ContextBuilder effect(Effect<U> effect)
    {
        sane(effect, "effect");
        return parentContext().effect(effect);
    }

    /**
     * Starts or adds an intent rule in this builder.
     *
     * @param cls
     *            Class of the rule subject.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> IntentBuilder<U> intent(Class<U> cls)
    {
        sane(cls, "cls");
        if (parent != null)
        {
            return parent.intent(cls);
        }
        return parent2.intent(cls);
    }

    /**
     * Starts or adds an intent rule in this builder.
     *
     * @param typeKey
     *            Type key identifying the rule subject.
     * @param <U>
     *            Type of the configured rule subject.
     * @return
     *            This builder for chaining.
     */
    public <U> IntentBuilder<U> intent(TypeKey<U> typeKey)
    {
        sane(typeKey, "typeKey");
        if (parent != null)
        {
            return parent.intent(typeKey);
        }
        return parent2.intent(typeKey);
    }

    /**
     * Registers an already-built intent on the parent context.
     *
     * @param intent
     *            Intent to register
     * @param typeKey
     *            Type key for the intent subject
     * @param <U>
     *            Subject type for the intent
     * @return Parent context builder for chaining
     */
    public <U> ContextBuilder intent(Intent<U> intent, TypeKey<U> typeKey)
    {
        sane(intent, "intent", typeKey, "typeKey");
        return parentContext().intent(intent, typeKey);
    }

    /**
     * Sets begin as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder begin()
    {
        return parentContext().begin();
    }

    /**
     * Sets commit as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder commit()
    {
        return parentContext().commit();
    }

    /**
     * Sets checkpoint as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder checkpoint()
    {
        return parentContext().checkpoint();
    }

    /**
     * Sets rollback as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder rollback()
    {
        return parentContext().rollback();
    }

    /**
     * Sets immediate as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder immediate()
    {
        return parentContext().immediate();
    }

    /**
     * Sets validation as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder validation()
    {
        return parentContext().validation();
    }

    /**
     * Starts configuring a transaction for this operation.
     *
     * @param name
     *            Business operation name.
     * @return
     *            This builder for chaining.
     */
    public TransactionBuilder transaction(String name)
    {
        sane(name, "name");
        return parentContext().transaction(name);
    }

    /**
     * Starts configuring a transaction for this operation.
     *
     * @param supplier
     *            Factory used to create the configured value.
     * @return
     *            This builder for chaining.
     */
    public TransactionBuilder transaction(Supplier<? extends Transaction> supplier)
    {
        sane(supplier, "supplier");
        return parentContext().transaction(supplier);
    }

    /**
     * Sets the factory used to create contexts.
     *
     * @param supplier
     *            Factory used to create the configured value.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder contextFactory(Supplier<? extends Context> supplier)
    {
        sane(supplier, "supplier");
        return parentContext().contextFactory(supplier);
    }

    /**
     * Adds or selects a context for this configuration.
     *
     * @param name
     *            Business operation name.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder context(String name)
    {
        sane(name, "name");
        if (parent != null)
        {
            return parent.context(name);
        }
        return parent2.context(name);
    }

    /**
     * Adds or selects a context for this configuration.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder context()
    {
        if (parent != null)
        {
            return parent.context();
        }
        return parent2.contextBuilder();
    }

    protected void checkBuilder()
    {
        if (parent != null)
        {
            parent.parent.verifyConfigurable();
        }
        else
        {
            parent2.parent.parent.verifyConfigurable();
        }
    }

    /**
     * Returns to the parent transaction builder.
     * @return
     *            This builder for chaining.
     */
    public TransactionBuilder doneTran()
    {
        return parent2;
    }

    protected ContextBuilder parentContext()
    {
        if (parent != null)
        {
            return parent;
        }
        return parent2.contextBuilder();
    }
}
