package com.taitl.existential.builders;

import com.taitl.ex.common.creator.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Builds {@link Invariant} rules.
 *
 * @param <T> Type to apply the invariants
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

    /**
     * Creates an invariant builder for a context or transaction.
     *
     * @param parent
     *            Parent builder that owns this configuration.
     * @param typeKey
     *            Type key identifying the rule subject.
     */
    public InvariantBuilder(ContextBuilder parent, TypeKey<T> typeKey)
    {
        sane(parent, "parent", typeKey, "typeKey");
        this.parent = parent;
        this.target = Creator.create(Invariant.class, new Class<?>[] { TypeKey.class }, typeKey);
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

    /**
     * Creates an invariant builder for a context or transaction.
     *
     * @param parent2
     *            Parent builder that owns this configuration.
     * @param typeKey
     *            Type key identifying the rule subject.
     */
    public InvariantBuilder(TransactionBuilder parent2, TypeKey<T> typeKey)
    {
        sane(parent2, "parent2", typeKey, "typeKey");
        this.parent2 = parent2;
        this.target = Creator.create(Invariant.class, new Class<?>[] { TypeKey.class }, typeKey);
    }

    /**
     * Sets the type key for this rule.
     *
     * @param typeKey
     *            Type key identifying the rule subject.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> typeKey(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        checkBuilder();
        target.typeKey(typeKey);
        return this;
    }

    /**
     * Adds a create invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> create(Predicate<? super T> condition)
    {
        checkBuilder();
        target.create(condition);
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
        sane(condition, "condition");
        checkBuilder();
        target.create(condition, description);
        return this;
    }

    /**
     * Adds a delete invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> delete(Predicate<? super T> condition)
    {
        checkBuilder();
        target.delete(condition);
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
        sane(condition, "condition");
        checkBuilder();
        target.delete(condition, description);
        return this;
    }

    /**
     * Adds a transit invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> transit(Predicate<? super T> condition)
    {
        checkBuilder();
        target.transit(condition);
        return this;
    }

    /**
     * Adds a transit invariant using a single-entity predicate.
     *
     * @param condition
     *            Predicate to validate on transit
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> transit(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.transit(condition, description);
        return this;
    }

    /**
     * Adds a transit invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> transit(BiPredicate<? super T, ? super T> condition)
    {
        checkBuilder();
        target.transit(condition);
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
        sane(condition, "condition");
        checkBuilder();
        target.transit(condition, description);
        return this;
    }

    /**
     * Adds a read invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> read(Predicate<? super T> condition)
    {
        checkBuilder();
        target.read(condition);
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
        sane(condition, "condition");
        checkBuilder();
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
    public InvariantBuilder<T> rl(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.rl(condition, description);
        return this;
    }

    /**
     * Adds a rl invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> rl(Predicate<? super T> condition)
    {
        checkBuilder();
        target.rl(condition);
        return this;
    }

    /**
     * Adds a write invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> write(Predicate<? super T> condition)
    {
        checkBuilder();
        target.write(condition);
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
        sane(condition, "condition");
        checkBuilder();
        target.write(condition, description);
        return this;
    }

    /**
     * Adds a port invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> port(Predicate<? super T> condition)
    {
        checkBuilder();
        target.port(condition);
        return this;
    }

    /**
     * Adds a port invariant with a description.
     *
     * @param condition
     *            Predicate to validate on port
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> port(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.port(condition, description);
        return this;
    }

    /**
     * Adds a port invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> port(BiPredicate<? super T, ? super T> condition)
    {
        checkBuilder();
        target.port(condition);
        return this;
    }

    /**
     * Adds a port invariant using a two-entity predicate.
     *
     * @param condition
     *            Predicate to validate on port
     * @param description
     *            Human-friendly description of the rule
     * @return This builder for chaining
     */
    public InvariantBuilder<T> port(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.port(condition, description);
        return this;
    }

    /**
     * Adds a update invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> update(Predicate<? super T> condition)
    {
        checkBuilder();
        target.update(condition);
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
        sane(condition, "condition");
        checkBuilder();
        target.update(condition, description);
        return this;
    }

    /**
     * Adds a cu invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> cu(Predicate<? super T> condition)
    {
        checkBuilder();
        target.cu(condition);
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
    public InvariantBuilder<T> cu(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.cu(condition, description);
        return this;
    }

    /**
     * Adds a cud invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> cud(Predicate<? super T> condition)
    {
        checkBuilder();
        target.cud(condition);
        return this;
    }

    /**
     * Adds a cud invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> cud(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.cud(condition, description);
        return this;
    }

    /**
     * Adds a ud invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> ud(Predicate<? super T> condition)
    {
        checkBuilder();
        target.ud(condition);
        return this;
    }

    /**
     * Adds a ud invariant.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param description
     *            Human-readable description of the rule.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> ud(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        checkBuilder();
        target.ud(condition, description);
        return this;
    }

    /**
     * Adds an all-entities invariant evaluated against the current entity type.
     *
     * @param predicate
     *            Predicate to enforce for all entities
     * @param description
     *            Description of invariant
     * @return This builder for chaining
     */
    public InvariantBuilder<T> all(Predicate<? super T> predicate, String description)
    {
        sane(predicate, "predicate");
        checkBuilder();
        target.all(predicate, description);
        return this;
    }

    /**
     * Adds an all quantifier rule.
     *
     * @param predicate
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> all(Predicate<? super T> predicate)
    {
        checkBuilder();
        target.all(predicate);
        return this;
    }

    /**
     * Adds an all-entities invariant evaluated on entities matching the condition.
     *
     * @param condition
     *            Predicate selecting entities to check
     * @param predicate
     *            Predicate to enforce on selected entities
     * @param description
     *            Description of invariant
     * @return This builder for chaining
     */
    public InvariantBuilder<T> all(Predicate<? super T> condition, Predicate<? super T> predicate, String description)
    {
        sane(condition, "condition", predicate, "predicate");
        checkBuilder();
        target.all(condition, predicate, description);
        return this;
    }

    /**
     * Adds an all quantifier rule.
     *
     * @param condition
     *            Condition that determines whether the rule applies.
     * @param predicate
     *            Condition that determines whether the rule applies.
     * @return
     *            This builder for chaining.
     */
    public InvariantBuilder<T> all(Predicate<? super T> condition, Predicate<? super T> predicate)
    {
        checkBuilder();
        target.all(condition, predicate);
        return this;
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @return This builder for chaining
     */
    public <K> InvariantBuilder<T> exists(Map<T, K> map)
    {
        sane(map, "map");
        checkBuilder();
        target.exists(map, "");
        return this;
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public <K> InvariantBuilder<T> exists(Map<T, K> map, String description)
    {
        sane(map, "map");
        checkBuilder();
        target.exists(map, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param predicate Predicate to evaluate against each value
     * @return This builder for chaining
     */
    public <K> InvariantBuilder<T> exists(Map<T, K> map, Predicate<T> predicate)
    {
        sane(map, "map", predicate, "predicate");
        checkBuilder();
        target.exists(map, predicate, "");
        return this;
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param predicate Predicate to evaluate against each value
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public <K> InvariantBuilder<T> exists(Map<T, K> map, Predicate<T> predicate, String description)
    {
        sane(map, "map", predicate, "predicate");
        checkBuilder();
        target.exists(map, predicate, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param bipredicate Predicate evaluated against key and mapped value
     * @return This builder for chaining
     */
    public <K> InvariantBuilder<T> exists(Map<T, K> map, BiPredicate<T, K> bipredicate)
    {
        sane(map, "map", bipredicate, "bipredicate");
        checkBuilder();
        target.exists(map, bipredicate, "");
        return this;
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param bipredicate Predicate evaluated against key and mapped value
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public <K> InvariantBuilder<T> exists(Map<T, K> map, BiPredicate<T, K> bipredicate, String description)
    {
        sane(map, "map", bipredicate, "bipredicate");
        checkBuilder();
        target.exists(map, bipredicate, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param predicate Predicate evaluated against the matching collection entries
     * @param placeholder Overload disambiguator
     * @return This builder for chaining
     */
    public <K> InvariantBuilder<T> exists(Map<T, K> map, Predicate<Collection<T>> predicate, int placeholder)
    {
        sane(map, "map", predicate, "predicate");
        checkBuilder();
        target.exists(map, predicate, placeholder, "");
        return this;
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param predicate Predicate evaluated against the matching collection entries
     * @param placeholder Overload disambiguator
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public <K> InvariantBuilder<T> exists(Map<T, K> map, Predicate<Collection<T>> predicate, int placeholder,
            String description)
    {
        sane(map, "map", predicate, "predicate");
        checkBuilder();
        target.exists(map, predicate, placeholder, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching map keys
     * @param placeholder Overload disambiguator
     * @return This builder for chaining
     */
    public <K> InvariantBuilder<T> exists(Map<T, K> map, BiPredicate<T, Collection<T>> bipredicate,
            int placeholder)
    {
        sane(map, "map", bipredicate, "bipredicate");
        checkBuilder();
        target.exists(map, bipredicate, placeholder, "");
        return this;
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching map keys
     * @param placeholder Overload disambiguator
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public <K> InvariantBuilder<T> exists(Map<T, K> map, BiPredicate<T, Collection<T>> bipredicate,
            int placeholder, String description)
    {
        sane(map, "map", bipredicate, "bipredicate");
        checkBuilder();
        target.exists(map, bipredicate, placeholder, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a set.
     *
     * @param set Set to evaluate
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Set<T> set)
    {
        sane(set, "set");
        checkBuilder();
        target.exists(set, "");
        return this;
    }

    /**
     * Creates an existential quantifier over a set.
     *
     * @param set Set to evaluate
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Set<T> set, String description)
    {
        sane(set, "set");
        checkBuilder();
        target.exists(set, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a set.
     *
     * @param set Set to evaluate
     * @param predicate Predicate to evaluate against each value
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Set<T> set, Predicate<T> predicate)
    {
        sane(set, "set", predicate, "predicate");
        checkBuilder();
        target.exists(set, predicate, "");
        return this;
    }

    /**
     * Creates an existential quantifier over a set.
     *
     * @param set Set to evaluate
     * @param predicate Predicate to evaluate against each value
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Set<T> set, Predicate<T> predicate, String description)
    {
        sane(set, "set", predicate, "predicate");
        checkBuilder();
        target.exists(set, predicate, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a set.
     *
     * @param set Set to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching set value
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Set<T> set, BiPredicate<T, T> bipredicate)
    {
        sane(set, "set", bipredicate, "bipredicate");
        checkBuilder();
        target.exists(set, bipredicate, "");
        return this;
    }

    /**
     * Creates an existential quantifier over a set.
     *
     * @param set Set to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching set value
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Set<T> set, BiPredicate<T, T> bipredicate, String description)
    {
        sane(set, "set", bipredicate, "bipredicate");
        checkBuilder();
        target.exists(set, bipredicate, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a set.
     *
     * @param set Set to evaluate
     * @param predicate Predicate evaluated against the matching set entries
     * @param placeholder Overload disambiguator
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Set<T> set, Predicate<Set<T>> predicate, String placeholder,
            String description)
    {
        sane(set, "set", predicate, "predicate", placeholder, "placeholder");
        checkBuilder();
        target.exists(set, predicate, placeholder, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a set.
     *
     * @param set Set to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching set entries
     * @param placeholder Overload disambiguator
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Set<T> set, BiPredicate<T, Set<T>> bipredicate,
            String placeholder, String description)
    {
        sane(set, "set", bipredicate, "bipredicate", placeholder, "placeholder");
        checkBuilder();
        target.exists(set, bipredicate, placeholder, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Collection<T> coll, String description)
    {
        sane(coll, "coll");
        checkBuilder();
        target.exists(coll, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Collection<T> coll)
    {
        sane(coll, "coll");
        checkBuilder();
        target.exists(coll, "");
        return this;
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param predicate Predicate to evaluate against each value
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Collection<T> coll, Predicate<T> predicate, String description)
    {
        sane(coll, "coll", predicate, "predicate");
        checkBuilder();
        target.exists(coll, predicate, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param predicate Predicate to evaluate against each value
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Collection<T> coll, Predicate<T> predicate)
    {
        sane(coll, "coll", predicate, "predicate");
        checkBuilder();
        target.exists(coll, predicate, "");
        return this;
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching collection value
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Collection<T> coll, BiPredicate<T, T> bipredicate, String description)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        checkBuilder();
        target.exists(coll, bipredicate, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching collection value
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Collection<T> coll, BiPredicate<T, T> bipredicate)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        checkBuilder();
        target.exists(coll, bipredicate, "");
        return this;
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param predicate Predicate evaluated against the matching collection entries
     * @param placeholder Overload disambiguator
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Collection<T> coll, Predicate<Collection<T>> predicate, int placeholder,
            String description)
    {
        sane(coll, "coll", predicate, "predicate");
        checkBuilder();
        target.exists(coll, predicate, placeholder, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param predicate Predicate evaluated against the matching collection entries
     * @param placeholder Overload disambiguator
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Collection<T> coll, Predicate<Collection<T>> predicate, int placeholder)
    {
        sane(coll, "coll", predicate, "predicate");
        checkBuilder();
        target.exists(coll, predicate, placeholder, "");
        return this;
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching collection entries
     * @param placeholder Overload disambiguator
     * @param description Description of invariant
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Collection<T> coll, BiPredicate<T, Collection<T>> bipredicate,
            int placeholder, String description)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        checkBuilder();
        target.exists(coll, bipredicate, placeholder, description);
        return this;
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching collection entries
     * @param placeholder Overload disambiguator
     * @return This builder for chaining
     */
    public InvariantBuilder<T> exists(Collection<T> coll, BiPredicate<T, Collection<T>> bipredicate,
            int placeholder)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        checkBuilder();
        target.exists(coll, bipredicate, placeholder, "");
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

    /**
     * Returns the parent transaction builder for chaining.
     *
     * @return Parent transaction builder
     */
    protected void checkBuilder()
    {
        if (parent != null)
        {
            parent.parent.verifyConfigurable();
            target.requireDescriptions(parent.parent.requireBehaviorDescriptions());
        }
        else
        {
            parent2.parent.parent.verifyConfigurable();
            target.requireDescriptions(parent2.parent.parent.requireBehaviorDescriptions());
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
