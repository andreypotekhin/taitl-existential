package com.taitl.existential.builders;

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
        sane(condition, "condition", description, "description");
        target.port(condition, description);
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
        sane(condition, "condition", description, "description");
        target.port(condition, description);
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
    public InvariantBuilder<T> cu(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        target.cu(condition, description);
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
        sane(predicate, "predicate", description, "description");
        target.all(predicate, description);
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
        sane(condition, "condition", predicate, "predicate", description, "description");
        target.all(condition, predicate, description);
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
        sane(map, "map", description, "description");
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
        sane(map, "map", predicate, "predicate", description, "description");
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
        sane(map, "map", bipredicate, "bipredicate", description, "description");
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
        sane(map, "map", predicate, "predicate", description, "description");
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
        sane(map, "map", bipredicate, "bipredicate", description, "description");
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
        sane(set, "set", description, "description");
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
        sane(set, "set", predicate, "predicate", description, "description");
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
        sane(set, "set", bipredicate, "bipredicate", description, "description");
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
        sane(set, "set", predicate, "predicate", placeholder, "placeholder", description, "description");
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
        sane(set, "set", bipredicate, "bipredicate", placeholder, "placeholder", description, "description");
        target.exists(set, bipredicate, placeholder, description);
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
        target.exists(coll, "");
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
        sane(coll, "coll", description, "description");
        target.exists(coll, description);
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
        target.exists(coll, predicate, "");
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
        sane(coll, "coll", predicate, "predicate", description, "description");
        target.exists(coll, predicate, description);
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
        target.exists(coll, bipredicate, "");
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
        sane(coll, "coll", bipredicate, "bipredicate", description, "description");
        target.exists(coll, bipredicate, description);
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
        target.exists(coll, predicate, placeholder, "");
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
        sane(coll, "coll", predicate, "predicate", description, "description");
        target.exists(coll, predicate, placeholder, description);
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
        target.exists(coll, bipredicate, placeholder, "");
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
        sane(coll, "coll", bipredicate, "bipredicate", description, "description");
        target.exists(coll, bipredicate, placeholder, description);
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

    public <U> InvariantBuilder<U> invariant(Class<U> cls)
    {
        sane(cls, "cls");
        if (parent != null)
        {
            return parent.invariant(cls);
        }
        return parent2.invariant(cls);
    }

    public <U> InvariantBuilder<U> invariant(TypeKey<U> typeKey)
    {
        sane(typeKey, "typeKey");
        if (parent != null)
        {
            return parent.invariant(typeKey);
        }
        return parent2.invariant(typeKey);
    }

    public <U> ContextBuilder invariant(Invariant<U> invariant)
    {
        sane(invariant, "invariant");
        return parentContext().invariant(invariant);
    }

    public <U> EffectBuilder<U> effect(Class<U> cls)
    {
        sane(cls, "cls");
        if (parent != null)
        {
            return parent.effect(cls);
        }
        return parent2.effect(cls);
    }

    public <U> EffectBuilder<U> effect(TypeKey<U> typeKey)
    {
        sane(typeKey, "typeKey");
        if (parent != null)
        {
            return parent.effect(typeKey);
        }
        return parent2.effect(typeKey);
    }

    public <U> ContextBuilder effect(Effect<U> effect)
    {
        sane(effect, "effect");
        return parentContext().effect(effect);
    }

    public <U> IntentBuilder<U> intent(Class<U> cls)
    {
        sane(cls, "cls");
        if (parent != null)
        {
            return parent.intent(cls);
        }
        return parent2.intent(cls);
    }

    public <U> IntentBuilder<U> intent(TypeKey<U> typeKey)
    {
        sane(typeKey, "typeKey");
        if (parent != null)
        {
            return parent.intent(typeKey);
        }
        return parent2.intent(typeKey);
    }

    public <U> ContextBuilder intent(Intent<U> intent)
    {
        sane(intent, "intent");
        return parentContext().intent(intent);
    }

    public ContextBuilder begin()
    {
        return parentContext().begin();
    }

    public ContextBuilder commit()
    {
        return parentContext().commit();
    }

    public ContextBuilder checkpoint()
    {
        return parentContext().checkpoint();
    }

    public ContextBuilder rollback()
    {
        return parentContext().rollback();
    }

    public ContextBuilder immediate()
    {
        return parentContext().immediate();
    }

    public ContextBuilder validation()
    {
        return parentContext().validation();
    }

    public TransactionBuilder transaction(String name)
    {
        sane(name, "name");
        return parentContext().transaction(name);
    }

    public TransactionBuilder transaction(Supplier<? extends Transaction> supplier)
    {
        sane(supplier, "supplier");
        return parentContext().transaction(supplier);
    }

    public ContextBuilder contextFactory(Supplier<? extends Context> supplier)
    {
        sane(supplier, "supplier");
        return parentContext().contextFactory(supplier);
    }

    public ContextBuilder context(String name)
    {
        sane(name, "name");
        if (parent != null)
        {
            return parent.context(name);
        }
        return parent2.context(name);
    }

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
