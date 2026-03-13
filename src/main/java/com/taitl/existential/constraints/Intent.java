package com.taitl.existential.constraints;

import com.taitl.ex.common.helper.strings.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.access_handlers.*;
import com.taitl.existential.handlers.combined_event_handlers.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.lang.Generics.*;

/**
 * Declares an intent to carry out an action on entities of type T.
 *
 * Intents are used to control what actions can be performed on entities of a given type.
 * If an intent is declared on a type, it is expected to be declared on all other types
 * for which same action may be performed in the course of business transaction.
 * Example: if 'read' intent is declared for type A, any attempt to read an entity of type B
 * will be denied, unless you also declare 'read' intent for type B.
 *
 * Thus, the intents work similarly to firewall rules, creating a 'deny-by-default' policy
 * around application entities.
 *
 * Intents are evaluated immediately on trigger event.
 *
 * @param <T>
 *            Entity type the intent applies to
 */
public class Intent<T> implements Evs<T>, Constraints<T>
{
    Transaction transaction;
    TypeKey<T> typeKey;
    List<Ev<T>> evs = new ArrayList<>();
    boolean requireDescriptions;

    /**
     * Creates an intent for the entity type inferred from an anonymous subclass.
     */
    public Intent()
    {
        this.typeKey = inferTypeKeyFromAnonymousSubclass(getClass(), Intent.class, "Intent");
    }

    /**
     * Creates an intent for the provided entity type key.
     *
     * @param typeKey Entity type key
     */
    public Intent(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        this.typeKey = typeKey;
    }

    /**
     * Creates an intent for the provided entity class.
     *
     * @param typeClass Entity class
     */
    public Intent(Class<T> typeClass)
    {
        sane(typeClass, "typeClass");
        this.typeKey = new TypeKey<>(typeClass);
    }

    /**
     * Declares intent to act on entities of this type.
     * (indicates that On event may be sent during transaction).
     *
     * @return This intent for chaining
     */
    public Intent<T> on()
    {
        return on(value -> true);
    }

    /**
     * Declares intent to act on entities of this type.
     * (indicates that On event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @return This intent for chaining
     */
    public Intent<T> on(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new On<T>(condition, null));
    }

    /**
     * Declares intent to act on entities of this type.
     * (indicates that On event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> on(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new On<T>(condition, null, description));
    }

    /**
     * Declares intent to create entities of this type.
     * (indicates that Create event may be sent during transaction).
     *
     * @return This intent for chaining
     */
    public Intent<T> create()
    {
        return create(value -> true);
    }

    /**
     * Declares intent to create entities of this type.
     * (indicates that Create event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @return This intent for chaining
     */
    public Intent<T> create(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnCreate<T>(condition, null));
    }

    /**
     * Declares intent to create entities of this type.
     * (indicates that Create event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> create(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnCreate<T>(condition, null, description));
    }

    /**
     * Declares intent to delete entities of this type.
     * (indicates that Delete event may be sent during transaction).
     *
     * @return This intent for chaining
     */
    public Intent<T> delete()
    {
        return delete(value -> true);
    }

    /**
     * Declares intent to delete entities of this type.
     * (indicates that Delete event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @return This intent for chaining
     */
    public Intent<T> delete(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnDelete<T>(condition, null));
    }

    /**
     * Declares intent to delete entities of this type.
     * (indicates that Delete event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> delete(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnDelete<T>(condition, null, description));
    }

    public Intent<T> transit()
    {
        return transit(value -> true);
    }

    public Intent<T> transit(Predicate<? super T> condition)
    {
        return transit(condition, null);
    }

    /**
     * Declares intent to transit entities of this type.
     * (indicates that Transit event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> transit(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnTransit<T>(condition, null, description));
    }

    public Intent<T> transit(BiPredicate<? super T, ? super T> condition)
    {
        return transit(condition, null);
    }

    /**
     * Declares intent to transit entities of this type.
     * (indicates that Transit event may be sent during transaction).
     *
     * @param condition Condition which old/new entity values must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> transit(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnTransit<T>(condition, null, description));
    }

    /**
     * Declares intent to read entities of this type.
     * (indicates that Read event may be sent during transaction).
     *
     * @return This intent for chaining
     */
    public Intent<T> read()
    {
        return read(value -> true);
    }

    /**
     * Declares intent to read entities of this type.
     * (indicates that Read event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @return This intent for chaining
     */
    public Intent<T> read(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnRead<T>(condition, null));
    }

    /**
     * Declares intent to read entities of this type.
     * (indicates that Read event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> read(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnRead<T>(condition, null, description));
    }

    /**
     * Declares intent to read-and-lock entities of this type.
     * (indicates that read-and-lock event may be sent during transaction).
     *
     * @return This intent for chaining
     */
    public Intent<T> rl()
    {
        return rl(value -> true);
    }

    /**
     * Declares intent to read-and-lock entities of this type.
     * (indicates that read-and-lock event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> rl(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnRL<T>(condition, null, description));
    }

    /**
     * Declares intent to read-and-lock entities of this type.
     * (indicates that read-and-lock event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @return This intent for chaining
     */
    public Intent<T> rl(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnRL<T>(condition, null));
    }

    /**
     * Declares intent to write entities of this type.
     * (indicates that Write event may be sent during transaction).
     *
     * @return This intent for chaining
     */
    public Intent<T> write()
    {
        return write(value -> true);
    }

    /**
     * Declares intent to write entities of this type.
     * (indicates that Write event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @return This intent for chaining
     */
    public Intent<T> write(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnWrite<T>(condition, null));
    }

    /**
     * Declares intent to write entities of this type.
     * (indicates that Write event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> write(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnWrite<T>(condition, null, description));
    }

    public Intent<T> port()
    {
        return port(value -> true);
    }

    public Intent<T> port(Predicate<? super T> condition)
    {
        return port(condition, null);
    }

    /**
     * Declares intent to port entities of this type.
     * (indicates that Port event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> port(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnPort<T>(condition, null, description));
    }

    public Intent<T> port(BiPredicate<? super T, ? super T> condition)
    {
        return port(condition, null);
    }

    /**
     * Declares intent to port entities of this type.
     * (indicates that Port event may be sent during transaction).
     *
     * @param condition Condition which old/new entity values must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> port(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnPort<T>(condition, null, description));
    }

    /**
     * Declares intent to update entities of this type.
     * (indicates that Update event may be sent during transaction).
     *
     * @return This intent for chaining
     */
    public Intent<T> update()
    {
        return update(value -> true);
    }

    /**
     * Declares intent to update entities of this type.
     * (indicates that Update event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @return This intent for chaining
     */
    public Intent<T> update(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnUpdate<T>(condition, null));
    }

    /**
     * Declares intent to update entities of this type.
     * (indicates that Update event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> update(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnUpdate<T>(condition, null, description));
    }

    /**
     * Declares intent to upsert entities of this type.
     * (indicates that Create-or-Update event may be sent during transaction).
     *
     * @return This intent for chaining
     */
    public Intent<T> cu()
    {
        return cu(value -> true);
    }

    /**
     * Declares intent to upsert entities of this type.
     * (indicates that Create-or-Update event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @return This intent for chaining
     */
    public Intent<T> cu(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnCU<T>(condition, null));
    }

    /**
     * Declares intent to upsert entities of this type.
     * (indicates that Create-or-Update event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> cu(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnCU<T>(condition, null, description));
    }

    public Intent<T> cud()
    {
        return cud(value -> true);
    }

    public Intent<T> cud(Predicate<? super T> condition)
    {
        return cud(condition, null);
    }

    public Intent<T> cud(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnCUD<T>(condition, null, description));
    }

    public Intent<T> ud()
    {
        return ud(value -> true);
    }

    public Intent<T> ud(Predicate<? super T> condition)
    {
        return ud(condition, null);
    }

    public Intent<T> ud(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition");
        validateDescription(description);
        return add(new OnUD<T>(condition, null, description));
    }

    /**
     * Adds an event intent to this constraint.
     *
     * @param ev Event intent to register
     * @return This intent for chaining
     */
    public Intent<T> add(Ev<T> ev)
    {
        sane(ev, "ev");
        evs.add(ev);
        return this;
    }

    public void requireDescriptions(boolean requireDescriptions)
    {
        this.requireDescriptions = requireDescriptions;
    }

    void validateDescription(String description)
    {
        Descriptions.require(requireDescriptions, description);
    }

    /**
     * Returns the event intents registered on this constraint.
     *
     * @return Ordered list of event intents
     */
    public List<Ev<T>> list()
    {
        return evs;
    }

    /**
     * Returns the entity type key this intent applies to.
     *
     * @return Entity type key
     */
    public TypeKey<T> typeKey()
    {
        return typeKey;
    }

    /**
     * Returns the parent transaction, if this intent is transaction-scoped.
     *
     * @return Parent transaction, if any
     */
    public Transaction transaction()
    {
        return transaction;
    }

    public boolean hasTransaction()
    {
        return transaction != null;
    }

    /**
     * Sets the parent transaction for this intent.
     *
     * @param transaction Parent transaction
     */
    public void transaction(Transaction transaction)
    {
        sane(transaction, "transaction");
        this.transaction = transaction;
    }

    /**
     * Sets the entity type key this intent applies to.
     *
     * @param typeKey Entity type key
     */
    public void typeKey(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        this.typeKey = typeKey;
    }
}
