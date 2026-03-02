package com.taitl.existential.constraints;

import com.taitl.existential.configs.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.access_handlers.*;
import com.taitl.existential.handlers.combined_event_handlers.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.quantifiers.*;

import java.util.*;
import java.util.function.*;

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
        return add(new On<T>(condition, null, description));
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
        sane(condition, "condition", description, "description");
        return add(new OnCreate<T>(condition, null, description));
    }

    /**
     * Declares an invariant for Change event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> change(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnChange<T>(condition, null, description));
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
        return add(new OnDelete<T>(condition, null, description));
    }

    /**
     * Declares an invariant for Modify event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> modify(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnModify<T>(condition, null, description));
    }

    /**
     * Declares an invariant for Mutate event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> mutate(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnMutate<T>(condition, null, description));
    }

    /**
     * Declares an invariant for Mutate event.
     *
     * @param condition Predicate to enforce for old/new entity values
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> mutate(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnMutate<T>(condition, null, description));
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
        return add(new OnRead<T>(condition, null, description));
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
        return add(new OnReadAndLock<T>(condition, null, description));
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
        return add(new OnWrite<T>(condition, null, description));
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
        return add(new OnPort<T>(condition, null, description));
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
        return add(new OnPort<T>(condition, null, description));
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
        return add(new OnUpdate<T>(condition, null, description));
    }

    /**
     * Declares an invariant for Create-or-Update event.
     *
     * @param condition Predicate to enforce for the entities that received the event
     * @param description Description of invariant
     * @return This invariant for chaining
     */
    public Invariant<T> upsert(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        return add(new OnCU<T>(condition, null, description));
    }

    /* Expression methods */

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
        add(new All<T>(predicate, description));
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
        sane(condition, "condition", predicate, "predicate", description, "description");
        add(new All<T>(condition, predicate, description));
        return this;
    }

    /**
     * Creates an existential quantifier over provided values.
     *
     * @param values Values to evaluate
     * @param predicate Predicate evaluated against each value
     * @return Exists quantifier
     */
    public Exists<T> exists(Collection<T> values, Predicate<T> predicate)
    {
        sane(values, "values", predicate, "predicate");
        return new Exists<T>(values, predicate);
    }

    /**
     * Creates an existential quantifier over provided values.
     *
     * @param values Values to evaluate
     * @param bipredicate Predicate evaluated against each value and transaction
     * @return Exists quantifier
     */
    public Exists<T> exists(Collection<T> values, BiPredicate<T, Transaction> bipredicate)
    {
        sane(values, "values", bipredicate, "bipredicate");
        return new Exists<T>(values, bipredicate);
    }

    /**
     * Creates an existential quantifier over the collection as a whole.
     *
     * @param values Values to evaluate
     * @param predicate Predicate evaluated against the full collection
     * @param placeholder Overload disambiguator
     * @return Exists quantifier
     */
    public Exists<T> exists(Collection<T> values, Predicate<Collection<T>> predicate, int placeholder)
    {
        sane(values, "values", predicate, "predicate");
        return new Exists<T>(values, predicate, placeholder);
    }

    /**
     * Creates an existential quantifier over the collection as a whole.
     *
     * @param values Values to evaluate
     * @param bipredicate Predicate evaluated against collection and transaction
     * @param placeholder Overload disambiguator
     * @return Exists quantifier
     */
    public Exists<T> exists(Collection<T> values, BiPredicate<Collection<T>, Transaction> bipredicate,
            int placeholder)
    {
        sane(values, "values", bipredicate, "bipredicate");
        return new Exists<T>(values, bipredicate, placeholder);
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
        evs.add(evaluable);
        return this;
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
