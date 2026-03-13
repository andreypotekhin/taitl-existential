package com.taitl.existential.constraints;

import com.taitl.ex.common.helper.*;
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
 * Declares side effects for entities of type T.
 * By default, effects are evaluated (carried out) in validation stage,
 * when a transaction is validated.
 * But they also can be attached at earlier stages (e.g. Immediate stage),
 * for immediate execution upon receiving the trigger event.
 * Be aware that doing so may affect performance, since during runtime
 * stage, every trigger event causes execution of the effect, 
 * whereas during validation stage, equal events are 'folded' into a
 * single event, evaluated once.
 * 
 * @param <T>
 *            Entity type the effect applies to
 */
public class Effect<T> implements Evs<T>, Immediate<T>, SideEffects<T>
{
    /**
     * Parent Transaction object, if any.
     * This field is null for effects that are not declared on
     * transaction level, e.g. for the effects declared in a context.
     */
    Transaction tran;
    TypeKey<T> typeKey;

    /**
     * Entity event handlers
     */
    List<Ev<T>> evs = new ArrayList<>();

    /**
     * Creates effects for the entity type inferred from an anonymous subclass.
     */
    public Effect()
    {
        this.typeKey = inferTypeKeyFromAnonymousSubclass(getClass(), Effect.class, "Effect");
    }

    /**
     * Creates effects for the provided entity type key.
     *
     * @param typeKey Entity type key
     */
    public Effect(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        this.typeKey = typeKey;
    }

    /**
     * Creates effects for the provided entity class.
     *
     * @param typeClass Entity class
     */
    public Effect(Class<T> typeClass)
    {
        sane(typeClass, "typeClass");
        this.typeKey = new TypeKey<>(typeClass);
    }

    /**
     * Expressions, such as All<T>, defined in this context.
     */
    // public Expressions expressions = new Expressions();

    /* Event handler methods */

    /**
     * Creates an effect for On event (that is, any event).
     *
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> on(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new On<T>(null, action));
    }

    /**
     * Creates an effect for On event (that is, any event).
     *
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> on(Consumer<? super T> action, String description)
    {
        sane(action, "action");
        return add(new On<T>(action, description));
    }

    /**
     * Creates an effect for On event (that is, any event).
     *
     * @param condition Condition (filter) on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> on(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new On<T>(condition, action));
    }

    /**
     * Creates an effect for On event (that is, any event).
     *
     * @param condition Condition (filter) on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> on(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new On<T>(condition, action, description));
    }

    /**
     * Creates an effect for Create event.
     *
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> create(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnCreate<T>(null, action));
    }

    /**
     * Creates an effect for Create event.
     *
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> create(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnCreate<T>(action, description));
    }

    /**
     * Creates an effect for Create event.
     *
     * @param condition Condition (filter) on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> create(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnCreate<T>(condition, action));
    }

    /**
     * Creates an effect for Create event.
     *
     * @param condition Condition (filter) on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> create(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnCreate<T>(condition, action, description));
    }

    /**
     * Creates an effect for Delete event.
     *
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> delete(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnDelete<T>(null, action));
    }

    /**
     * Creates an effect for Delete event.
     *
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> delete(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnDelete<T>(action, description));
    }

    /**
     * Creates an effect for Delete event.
     *
     * @param condition Condition on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> delete(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnDelete<T>(condition, action));
    }

    /**
     * Creates an effect for Delete event.
     *
     * @param condition Condition on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> delete(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnDelete<T>(condition, action, description));
    }

    /**
     * Creates an effect for Transit event.
     *
     * @param action Action to perform for old/new entity values
     * @return This effect for chaining
     */
    public Effect<T> transit(BiConsumer<? super T, ? super T> action)
    {
        sane(action, "action");
        return add(new OnTransit<T>(action));
    }

    /**
     * Creates an effect for Transit event.
     *
     * @param action Action to perform for old/new entity values
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> transit(BiConsumer<? super T, ? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnTransit<T>(action, description));
    }

    /**
     * Creates an effect for Transit event.
     *
     * @param condition Condition on current entities to which apply action
     * @param action Action to perform for old/new entity values
     * @return This effect for chaining
     */
    public Effect<T> transit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnTransit<T>(condition, action));
    }

    /**
     * Creates an effect for Transit event.
     *
     * @param condition Condition on current entities to which apply action
     * @param action Action to perform for old/new entity values
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> transit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnTransit<T>(condition, action, description));
    }

    /**
     * Creates an effect for Transit event.
     *
     * @param condition Condition on old/new entity values
     * @param action Action to perform for old/new entity values
     * @return This effect for chaining
     */
    public Effect<T> transit(BiPredicate<? super T, ? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnTransit<T>(condition, action));
    }

    /**
     * Creates an effect for Transit event.
     *
     * @param condition Condition on old/new entity values
     * @param action Action to perform for old/new entity values
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> transit(BiPredicate<? super T, ? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnTransit<T>(condition, action, description));
    }

    /**
     * Creates an effect for Read event.
     *
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> read(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnRead<T>(null, action));
    }

    /**
     * Creates an effect for Read event.
     *
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> read(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnRead<T>(action, description));
    }

    /**
     * Creates an effect for Read event.
     *
     * @param condition Condition on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> read(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnRead<T>(condition, action));
    }

    /**
     * Creates an effect for Read event.
     *
     * @param condition Condition on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> read(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnRead<T>(condition, action, description));
    }

    /**
     * Creates an effect for read-and-lock event.
     *
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> rl(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnRL<T>(action, description));
    }

    /**
     * Creates an effect for read-and-lock event.
     *
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> rl(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnRL<T>(null, action));
    }

    /**
     * Creates an effect for read-and-lock event.
     *
     * @param condition Condition on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> rl(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnRL<T>(condition, action));
    }

    /**
     * Creates an effect for read-and-lock event.
     *
     * @param condition Condition on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> rl(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnRL<T>(condition, action, description));
    }

    /**
     * Creates an effect for Write event.
     *
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> write(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnWrite<T>(null, action));
    }

    /**
     * Creates an effect for Write event.
     *
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> write(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnWrite<T>(action, description));
    }

    /**
     * Creates an effect for Write event.
     *
     * @param condition Condition on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> write(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnWrite<T>(condition, action));
    }

    /**
     * Creates an effect for Write event.
     *
     * @param condition Condition on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> write(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnWrite<T>(condition, action, description));
    }

    /**
     * Creates an effect for Port event.
     *
     * @param action Action to perform for old/new entity values
     * @return This effect for chaining
     */
    public Effect<T> port(BiConsumer<? super T, ? super T> action)
    {
        sane(action, "action");
        return add(new OnPort<T>(action));
    }

    /**
     * Creates an effect for Port event.
     *
     * @param action Action to perform for old/new entity values
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> port(BiConsumer<? super T, ? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnPort<T>(action, description));
    }

    /**
     * Creates an effect for Port event.
     *
     * @param condition Condition on current entities to which apply action
     * @param action Action to perform for old/new entity values
     * @return This effect for chaining
     */
    public Effect<T> port(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnPort<T>(condition, action));
    }

    /**
     * Creates an effect for Port event.
     *
     * @param condition Condition on current entities to which apply action
     * @param action Action to perform for old/new entity values
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> port(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnPort<T>(condition, action, description));
    }

    /**
     * Creates an effect for Port event.
     *
     * @param condition Condition on old/new entity values
     * @param action Action to perform for old/new entity values
     * @return This effect for chaining
     */
    public Effect<T> port(BiPredicate<? super T, ? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnPort<T>(condition, action));
    }

    /**
     * Creates an effect for Port event.
     *
     * @param condition Condition on old/new entity values
     * @param action Action to perform for old/new entity values
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> port(BiPredicate<? super T, ? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnPort<T>(condition, action, description));
    }

    /**
     * Creates an effect for Update event.
     *
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> update(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnUpdate<T>(null, action));
    }

    /**
     * Creates an effect for Update event.
     *
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> update(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnUpdate<T>(action, description));
    }

    /**
     * Creates an effect for Update event.
     *
     * @param condition Condition on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> update(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnUpdate<T>(condition, action));
    }

    /**
     * Creates an effect for Update event.
     *
     * @param condition Condition on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> update(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnUpdate<T>(condition, action, description));
    }

    /**
     * Creates an effect for Create-or-Update event.
     *
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> cu(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnCU<T>(null, action));
    }

    /**
     * Creates an effect for Create-or-Update event.
     *
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> cu(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnCU<T>(action, description));
    }

    /**
     * Creates an effect for Create-or-Update event.
     *
     * @param condition Condition on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @return This effect for chaining
     */
    public Effect<T> cu(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnCU<T>(condition, action));
    }

    /**
     * Creates an effect for Create-or-Update event.
     *
     * @param condition Condition on the entities to which apply action
     * @param action Action to perform for the entities that received this event
     * @param description Description of effect
     * @return This effect for chaining
     */
    public Effect<T> cu(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnCU<T>(condition, action, description));
    }

    public Effect<T> cud(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnCUD<T>(null, action));
    }

    public Effect<T> cud(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnCUD<T>(action, description));
    }

    public Effect<T> cud(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnCUD<T>(condition, action));
    }

    public Effect<T> cud(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnCUD<T>(condition, action, description));
    }

    public Effect<T> ud(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnUD<T>(null, action));
    }

    public Effect<T> ud(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnUD<T>(action, description));
    }

    public Effect<T> ud(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnUD<T>(condition, action));
    }

    public Effect<T> ud(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnUD<T>(condition, action, description));
    }

    /* Evs implementation */

    /**
     * Adds a handler to this effect.
     *
     * @param ev
     *            Event handler to register
     * @return This effect for chaining
     */
    public Effect<T> add(Ev<T> ev)
    {
        sane(ev, "eh");
        evs.add(ev);
        return this;
    }

    /**
     * Returns the handlers registered with this effect.
     *
     * @return Ordered list of handlers
     */
    public List<Ev<T>> list()
    {
        return evs;
    }

    /**
     * Returns the entity type key this effect applies to.
     *
     * @return Entity type key
     */
    public TypeKey<T> typeKey()
    {
        return typeKey;
    }

    /* Attributes */

    /**
     * Returns the transaction associated with this effect.
     *
     * @return Transaction owning this effect
     */
    public Transaction getTransaction()
    {
        State.cool(tran, "tran");
        return tran;
    }

    public boolean hasTransaction()
    {
        return tran != null;
    }

    /**
     * Associates this effect with a transaction.
     *
     * @param tr
     *            Transaction owning this effect
     */
    public void setTransaction(Transaction tr)
    {
        sane(tr, "tr");
        tran = tr;
    }

    /**
     * Sets the entity type key this effect applies to.
     *
     * @param typeKey Entity type key
     */
    public void typeKey(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        this.typeKey = typeKey;
    }
}
