package com.taitl.existential.builders;

import com.taitl.existential.constraints.*;
import com.taitl.existential.keys.*;

import java.util.function.*;

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
    Invariant<T> target;

    /**
     * Creates an invariant builder bound to a context builder.
     *
     * @param parent
     *            Parent context builder
     */
    public InvariantBuilder(ContextBuilder parent)
    {
        sane(parent, "parent");
        throw new IllegalStateException("InvariantBuilder requires a TypeKey. Use ContextBuilder.invariant(Class<T>) "
                + "or ContextBuilder.invariant(TypeKey<T>).");
    }

    public InvariantBuilder(ContextBuilder parent, TypeKey<T> typeKey)
    {
        sane(parent, "parent", typeKey, "typeKey");
        this.parent = parent;
        this.target = new Invariant<>(typeKey);
    }

    /**
     * Creates an invariant builder bound to a transaction builder.
     *
     * @param parent2
     *            Parent transaction builder
     */
    public InvariantBuilder(TransactionBuilder parent2)
    {
        sane(parent2, "parent2");
        throw new IllegalStateException(
                "InvariantBuilder requires a TypeKey. Use TransactionBuilder.invariant(Class<T>) "
                        + "or TransactionBuilder.invariant(TypeKey<T>).");
    }

    public InvariantBuilder(TransactionBuilder parent2, TypeKey<T> typeKey)
    {
        sane(parent2, "parent2", typeKey, "typeKey");
        this.parent2 = parent2;
        this.target = new Invariant<>(typeKey);
    }

    public InvariantBuilder<T> typeKey(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        target.typeKey(typeKey);
        return this;
    }

    /**
     * Adds a create invariant with a description.
     *
     * @param condition
     *            Predicate to validate on create
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> create(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.create(condition, description);
        return this;
    }

    /**
     * Adds a delete invariant with a description.
     *
     * @param condition
     *            Predicate to validate on delete
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> delete(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.delete(condition, description);
        return this;
    }

    /**
     * Adds a mutate invariant using a single-entity predicate.
     *
     * @param condition
     *            Predicate to validate on mutate
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> mutate(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.mutate(condition, description);
        return this;
    }

    /**
     * Adds a mutate invariant using a two-entity predicate.
     *
     * @param condition
     *            Predicate to validate on mutate
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> mutate(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.mutate(condition, description);
        return this;
    }

    /**
     * Adds a read invariant with a description.
     *
     * @param condition
     *            Predicate to validate on read
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> read(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.read(condition, description);
        return this;
    }

    /**
     * Adds a read-and-lock invariant with a description.
     *
     * @param condition
     *            Predicate to validate on read-and-lock
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> readAndLock(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.readAndLock(condition, description);
        return this;
    }

    /**
     * Adds a write invariant with a description.
     *
     * @param condition
     *            Predicate to validate on write
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> write(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.write(condition, description);
        return this;
    }

    /**
     * Adds a transit invariant with a description.
     *
     * @param condition
     *            Predicate to validate on transit
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> transit(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.transit(condition, description);
        return this;
    }

    /**
     * Adds a transit invariant using a two-entity predicate.
     *
     * @param condition
     *            Predicate to validate on transit
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> transit(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.transit(condition, description);
        return this;
    }

    /**
     * Adds an update invariant with a description.
     *
     * @param condition
     *            Predicate to validate on update
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> update(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.update(condition, description);
        return this;
    }

    /**
     * Adds an upsert invariant with a description.
     *
     * @param condition
     *            Predicate to validate on upsert
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> upsert(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.upsert(condition, description);
        return this;
    }

    /**
     * Builds the configured invariant.
     *
     * @return Configured invariant
     */
    public Invariant<T> build()
    {
        return target;
    }

    /**
     * Returns the parent context builder for chaining.
     *
     * @return Parent context builder
     */
    public ContextBuilder done()
    {
        return parent;
    }

    /**
     * Returns the parent transaction builder for chaining.
     *
     * @return Parent transaction builder
     */
    public TransactionBuilder doneTran()
    {
        return parent2;
    }
}
