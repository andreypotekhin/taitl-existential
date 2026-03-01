package com.taitl.existential.constraints;

import com.taitl.existential.configs.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.access_handlers.*;
import com.taitl.existential.handlers.combined_event_handlers.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

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

    public Intent()
    {
        this.typeKey = inferTypeKeyFromAnonymousSubclass(getClass(), Intent.class, "Intent");
    }

    public Intent(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        this.typeKey = typeKey;
    }

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
        sane(condition, "condition", description, "description");
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
        sane(condition, "condition", description, "description");
        return add(new OnCreate<T>(condition, null, description));
    }

    /**
     * Declares intent to change entities of this type.
     * (indicates that Change event may be sent during transaction).
     *
     * @return This intent for chaining
     */
    public Intent<T> change()
    {
        return change(value -> true);
    }

    /**
     * Declares intent to change entities of this type.
     * (indicates that Change event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @return This intent for chaining
     */
    public Intent<T> change(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnChange<T>(condition, null));
    }

    /**
     * Declares intent to change entities of this type.
     * (indicates that Change event may be sent during transaction).
     *
     * @param condition Condition which the entities that receive this event must satisfy
     * @param description Description of intent
     * @return This intent for chaining
     */
    public Intent<T> change(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnChange<T>(condition, null, description));
    }

    public Intent<T> delete()
    {
        return delete(value -> true);
    }

    public Intent<T> delete(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnDelete<T>(condition, null));
    }

    public Intent<T> delete(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnDelete<T>(condition, null, description));
    }

    public Intent<T> modify()
    {
        return modify(value -> true);
    }

    public Intent<T> modify(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnModify<T>(condition, null));
    }

    public Intent<T> modify(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnModify<T>(condition, null, description));
    }

    public Intent<T> mutate(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnMutate<T>(condition, null, description));
    }

    public Intent<T> mutate(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnMutate<T>(condition, null, description));
    }

    public Intent<T> read()
    {
        return read(value -> true);
    }

    public Intent<T> read(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnRead<T>(condition, null));
    }

    public Intent<T> read(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnRead<T>(condition, null, description));
    }

    public Intent<T> readAndLock()
    {
        return readAndLock(value -> true);
    }

    public Intent<T> readAndLock(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnReadAndLock<T>(condition, null));
    }

    public Intent<T> readAndLock(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnReadAndLock<T>(condition, null, description));
    }

    public Intent<T> write()
    {
        return write(value -> true);
    }

    public Intent<T> write(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnWrite<T>(condition, null));
    }

    public Intent<T> write(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnWrite<T>(condition, null, description));
    }

    public Intent<T> transit(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnTransit<T>(condition, null, description));
    }

    public Intent<T> transit(BiPredicate<? super T, ? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnTransit<T>(condition, null, description));
    }

    public Intent<T> update()
    {
        return update(value -> true);
    }

    public Intent<T> update(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnUpdate<T>(condition, null));
    }

    public Intent<T> update(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnUpdate<T>(condition, null, description));
    }

    public Intent<T> upsert()
    {
        return upsert(value -> true);
    }

    public Intent<T> upsert(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        return add(new OnCU<T>(condition, null));
    }

    public Intent<T> upsert(Predicate<? super T> condition, String description)
    {
        sane(condition, "condition", description, "description");
        return add(new OnCU<T>(condition, null, description));
    }

    public Intent<T> add(Ev<T> ev)
    {
        sane(ev, "ev");
        evs.add(ev);
        return this;
    }

    public List<Ev<T>> list()
    {
        return evs;
    }

    public TypeKey<T> typeKey()
    {
        return typeKey;
    }

    public Transaction transaction()
    {
        return transaction;
    }

    public void transaction(Transaction transaction)
    {
        sane(transaction, "transaction");
        this.transaction = transaction;
    }

    public void typeKey(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        this.typeKey = typeKey;
    }
}
