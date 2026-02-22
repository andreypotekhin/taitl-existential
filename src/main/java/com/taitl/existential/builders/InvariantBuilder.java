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

    /**
     * Creates an invariant builder bound to a context builder.
     *
     * @param parent
     *            Parent context builder
     */
    public InvariantBuilder(ContextBuilder parent)
    {
        this.parent = parent;
    }

    /**
     * Creates an invariant builder bound to a transaction builder.
     *
     * @param parent2
     *            Parent transaction builder
     */
    public InvariantBuilder(TransactionBuilder parent2)
    {
        this.parent2 = parent2;
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
     * Adds a change invariant with a description.
     *
     * @param condition
     *            Predicate to validate on change
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> change(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.change(condition, description);
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
     * Adds a modify invariant with a description.
     *
     * @param condition
     *            Predicate to validate on modify
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> modify(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.modify(condition, description);
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
