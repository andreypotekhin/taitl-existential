package com.taitl.existential.constraints;

import com.taitl.ex.common.helper.strings.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.access_handlers.*;
import com.taitl.existential.handlers.combined_event_handlers.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.quantifiers.*;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;
import static com.taitl.ex.common.helper.lang.Generics.*;

/**
 * Declares invariants over entity of type T.
 * Invariants are evaluated when business transaction is validated
 * (upon commit() or checkpoint() methods).
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
     * Entity type key.
     */
    TypeKey<T> typeKey;

    /**
     * Entity event handlers.
     */
    List<Ev<T>> evs = new ArrayList<>();
    boolean requireDescriptions;

    /**
     * Creates an invariant for the entity type inferred from an anonymous subclass.
     */
    public Invariant()
    {
        this.typeKey = inferTypeKeyFromAnonymousSubclass(getClass(), Invariant.class, "Invariant");
    }

    /**
     * Creates an invariant for the provided entity type key.
     *
     * @param typeKey Entity type key
     */
    public Invariant(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        this.typeKey = typeKey;
    }

    /**
     * Creates an invariant for the provided entity class.
     *
     * @param typeClass Entity class
     */
    public Invariant(Class<T> typeClass)
    {
        sane(typeClass, "typeClass");
        this.typeKey = new TypeKey<>(typeClass);
    }

    /* Event handler methods */

    public Invariant<T> on(Predicate<? super T> condition)
    {
        return on(condition, null);
    }

    /**
     * Declares an invariant for On event (that is, any event).
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> on(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new On<T>(condition, null, description));
    }

    public Invariant<T> create(Predicate<? super T> condition)
    {
        return create(condition, null);
    }

    /**
     * Declares an invariant for Create event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> create(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnCreate<T>(condition, null, description));
    }

    public Invariant<T> delete(Predicate<? super T> condition)
    {
        return delete(condition, null);
    }

    /**
     * Declares an invariant for Delete event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> delete(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnDelete<T>(condition, null, description));
    }

    public Invariant<T> transit(Predicate<? super T> condition)
    {
        return transit(condition, null);
    }

    /**
     * Declares an invariant for Transit event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> transit(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnTransit<T>(condition, null, description));
    }

    public Invariant<T> transit(BiPredicate<? super T, ? super T> condition)
    {
        return transit(condition, null);
    }

    /**
     * Declares an invariant for Transit event.
     *
     * @param condition Predicate to enforce for old/new entity values
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> transit(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnTransit<T>(condition, null, description));
    }

    public Invariant<T> read(Predicate<? super T> condition)
    {
        return read(condition, null);
    }

    /**
     * Declares an invariant for Read event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> read(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnRead<T>(condition, null, description));
    }

    public Invariant<T> readAndLock(Predicate<? super T> condition)
    {
        return readAndLock(condition, null);
    }

    /**
     * Declares an invariant for ReadAndLock event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> readAndLock(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnReadAndLock<T>(condition, null, description));
    }

    public Invariant<T> write(Predicate<? super T> condition)
    {
        return write(condition, null);
    }

    /**
     * Declares an invariant for Write event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> write(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnWrite<T>(condition, null, description));
    }

    public Invariant<T> port(Predicate<? super T> condition)
    {
        return port(condition, null);
    }

    /**
     * Declares an invariant for Port event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> port(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnPort<T>(condition, null, description));
    }

    public Invariant<T> port(BiPredicate<? super T, ? super T> condition)
    {
        return port(condition, null);
    }

    /**
     * Declares an invariant for Port event.
     *
     * @param condition Predicate to enforce for old/new entity values
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> port(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnPort<T>(condition, null, description));
    }

    public Invariant<T> update(Predicate<? super T> condition)
    {
        return update(condition, null);
    }

    /**
     * Declares an invariant for Update event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> update(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnUpdate<T>(condition, null, description));
    }

    public Invariant<T> cu(Predicate<? super T> condition)
    {
        return cu(condition, null);
    }

    /**
     * Declares an invariant for Create-or-Update event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> cu(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnCU<T>(condition, null, description));
    }

    public Invariant<T> cud(Predicate<? super T> condition)
    {
        return cud(condition, null);
    }

    public Invariant<T> cud(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnCUD<T>(condition, null, description));
    }

    public Invariant<T> ud(Predicate<? super T> condition)
    {
        return ud(condition, null);
    }

    public Invariant<T> ud(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnUD<T>(condition, null, description));
    }

    /* Expression methods */

    public Invariant<T> all(Predicate<? super T> predicate)
    {
        sane(predicate, "predicate");
        add(new All<T>(predicate));
        return this;
    }

    /**
     * Adds an all-entities invariant evaluated against the current entity type.
     *
     * @param predicate Predicate to enforce for all entities
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> all(Predicate<? super T> predicate, String description)
    {
        sane(predicate, "predicate");
        validateDescription(description);
        add(new All<T>(predicate, description));
        return this;
    }

    public Invariant<T> all(Predicate<? super T> condition, Predicate<? super T> predicate)
    {
        sane(condition, "condition", predicate, "predicate");
        add(new All<T>(condition, predicate));
        return this;
    }

    /**
     * Adds an all-entities invariant evaluated on entities matching the condition.
     *
     * @param condition Predicate selecting entities to check
     * @param predicate Predicate to enforce on selected entities
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> all(Predicate<? super T> condition, Predicate<? super T> predicate, String description)
    {
        sane(condition, "condition", predicate, "predicate");
        validateDescription(description);
        add(new All<T>(condition, predicate, description));
        return this;
    }

    public <D> Invariant<T> exists(Map<T, D> map)
    {
        return exists(map, (String) null);
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public <D> Invariant<T> exists(Map<T, D> map, String description)
    {
        sane(map, "map");
        validateDescription(description);
        add(new Exists<T>(map, description));
        return this;
    }

    public <D> Invariant<T> exists(Map<T, D> map, Predicate<T> predicate)
    {
        return exists(map, predicate, null);
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param predicate Predicate to evaluate against each value
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public <D> Invariant<T> exists(Map<T, D> map, Predicate<T> predicate, String description)
    {
        sane(map, "map", predicate, "predicate");
        validateDescription(description);
        add(new Exists<T>(map, predicate, description));
        return this;
    }

    public <D> Invariant<T> exists(Map<T, D> map, BiPredicate<T, D> bipredicate)
    {
        return exists(map, bipredicate, null);
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param bipredicate Predicate evaluated against each key and mapped value
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public <D> Invariant<T> exists(Map<T, D> map, BiPredicate<T, D> bipredicate, String description)
    {
        sane(map, "map", bipredicate, "bipredicate");
        validateDescription(description);
        add(new Exists<T>(map, bipredicate, description));
        return this;
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param predicate Predicate evaluated against matching map keys
     * @param placeholder Overload disambiguator
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public <D> Invariant<T> exists(Map<T, D> map, Predicate<Collection<T>> predicate, int placeholder,
            String description)
    {
        sane(map, "map", predicate, "predicate");
        validateDescription(description);
        add(new Exists<T>(map, predicate, placeholder, description));
        return this;
    }

    /**
     * Creates an existential quantifier over a map.
     *
     * @param map Map to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching map keys
     * @param placeholder Overload disambiguator
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public <D> Invariant<T> exists(Map<T, D> map, BiPredicate<T, Collection<T>> bipredicate,
            int placeholder, String description)
    {
        sane(map, "map", bipredicate, "bipredicate");
        validateDescription(description);
        add(new Exists<T>(map, bipredicate, placeholder, description));
        return this;
    }

    public Invariant<T> exists(Set<T> set)
    {
        return exists(set, (String) null);
    }

    /**
     * Creates an existential quantifier over a set.
     *
     * @param set Set to evaluate
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public Invariant<T> exists(Set<T> set, String description)
    {
        sane(set, "set");
        validateDescription(description);
        add(new Exists<T>(set, description));
        return this;
    }

    public Invariant<T> exists(Set<T> set, Predicate<T> predicate)
    {
        return exists(set, predicate, null);
    }

    /**
     * Creates an existential quantifier over a set.
     *
     * @param set Set to evaluate
     * @param predicate Predicate to evaluate against each value
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public Invariant<T> exists(Set<T> set, Predicate<T> predicate, String description)
    {
        sane(set, "set", predicate, "predicate");
        validateDescription(description);
        add(new Exists<T>(set, predicate, description));
        return this;
    }

    public Invariant<T> exists(Set<T> set, BiPredicate<T, T> bipredicate)
    {
        return exists(set, bipredicate, null);
    }

    /**
     * Creates an existential quantifier over a set.
     *
     * @param set Set to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching set value
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public Invariant<T> exists(Set<T> set, BiPredicate<T, T> bipredicate, String description)
    {
        sane(set, "set", bipredicate, "bipredicate");
        validateDescription(description);
        add(new Exists<T>(set, bipredicate, description));
        return this;
    }

    /**
     * Creates an existential quantifier over the set as a whole.
     *
     * @param set Values to evaluate
     * @param predicate Predicate evaluated against the matching set values
     * @param placeholder Overload disambiguator
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public Invariant<T> exists(Set<T> set, Predicate<Set<T>> predicate, String placeholder,
            String description)
    {
        sane(set, "set", predicate, "predicate", placeholder, "placeholder");
        validateDescription(description);
        add(new Exists<T>(set, predicate, placeholder, description));
        return this;
    }

    /**
     * Creates an existential quantifier over a set.
     *
     * @param set Set to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching set entries
     * @param placeholder Overload disambiguator
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public Invariant<T> exists(Set<T> set, BiPredicate<T, Set<T>> bipredicate,
            String placeholder, String description)
    {
        sane(set, "set", bipredicate, "bipredicate", placeholder, "placeholder");
        validateDescription(description);
        add(new Exists<T>(set, bipredicate, placeholder, description));
        return this;
    }

    public Invariant<T> exists(Collection<T> coll)
    {
        return exists(coll, (String) null);
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public Invariant<T> exists(Collection<T> coll, String description)
    {
        sane(coll, "coll");
        validateDescription(description);
        add(new Exists<T>(coll, description));
        return this;
    }

    public Invariant<T> exists(Collection<T> coll, Predicate<T> predicate)
    {
        return exists(coll, predicate, null);
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param predicate Predicate to evaluate against each value
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public Invariant<T> exists(Collection<T> coll, Predicate<T> predicate, String description)
    {
        sane(coll, "coll", predicate, "predicate");
        validateDescription(description);
        add(new Exists<T>(coll, predicate, description));
        return this;
    }

    public Invariant<T> exists(Collection<T> coll, BiPredicate<T, T> bipredicate)
    {
        return exists(coll, bipredicate, null);
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching collection value
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public Invariant<T> exists(Collection<T> coll, BiPredicate<T, T> bipredicate, String description)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        validateDescription(description);
        add(new Exists<T>(coll, bipredicate, description));
        return this;
    }

    /**
     * Creates an existential quantifier over the collection as a whole.
     *
     * @param coll Values to evaluate
     * @param predicate Predicate evaluated against the matching collection values
     * @param placeholder Overload disambiguator
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public Invariant<T> exists(Collection<T> coll, Predicate<Collection<T>> predicate, int placeholder,
            String description)
    {
        sane(coll, "coll", predicate, "predicate");
        validateDescription(description);
        add(new Exists<T>(coll, predicate, placeholder, description));
        return this;
    }

    /**
     * Creates an existential quantifier over a collection.
     *
     * @param coll Collection to evaluate
     * @param bipredicate Predicate evaluated against evaluated entity and matching collection entries
     * @param placeholder Overload disambiguator
     * @param description Description of invariant
     * @return Exists quantifier
     */
    public Invariant<T> exists(Collection<T> coll, BiPredicate<T, Collection<T>> bipredicate,
            int placeholder, String description)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        validateDescription(description);
        add(new Exists<T>(coll, bipredicate, placeholder, description));
        return this;
    }

    /* Evs implementation */

    /**
     * Adds an event invariant to this constraint.
     *
     * @param evaluable Event invariant to register
     * @return This invariant for chaining
     */
    public Invariant<T> add(Ev<T> evaluable)
    {
        sane(evaluable, "evaluable");
        Descriptions.require(requireDescriptions, evaluable);
        evs.add(evaluable);
        return this;
    }

    public void requireDescriptions(boolean requireDescriptions)
    {
        this.requireDescriptions = requireDescriptions;
        for (Ev<T> ev : evs)
        {
            Descriptions.require(requireDescriptions, ev);
        }
    }

    void validateDescription(String description)
    {
        Descriptions.require(requireDescriptions, description);
    }

    /**
     * Returns the event invariants registered on this constraint.
     *
     * @return Ordered list of event invariants
     */
    public List<Ev<T>> list()
    {
        return evs;
    }

    /**
     * Returns the entity type key this invariant applies to.
     *
     * @return Entity type key
     */
    public TypeKey<T> typeKey()
    {
        return typeKey;
    }

    /* Attributes */

    /**
     * Returns the parent transaction, if this invariant is transaction-scoped.
     *
     * @return Parent transaction
     */
    public Transaction transaction()
    {
        cool(tran, "tran");
        return tran;
    }

    public boolean hasTransaction()
    {
        return tran != null;
    }

    /**
     * Sets the parent transaction for this invariant.
     *
     * @param transaction Parent transaction
     */
    public void transaction(Transaction transaction)
    {
        sane(transaction, "transaction");
        tran = transaction;
    }

    /**
     * Sets the entity type key this invariant applies to.
     *
     * @param typeKey Entity type key
     */
    public void typeKey(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        this.typeKey = typeKey;
    }
}
