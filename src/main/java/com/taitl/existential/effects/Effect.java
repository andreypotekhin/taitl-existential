package com.taitl.existential.effects;

import com.taitl.existential.expressions.Expression;
import com.taitl.existential.expressions.Expressions;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;
import com.taitl.existential.instructions.Instructions;
import com.taitl.existential.rules.RuleSet;
import com.taitl.existential.transactions.Transaction;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Effect<T> implements RuleSet<T>
{
    /**
     * Parent Transaction object, if any.
     *
     * This field is null for invariants that are not declared on
     * transaction level, e.g. for invariants declared at a context.
     */
    private Transaction tran;

    /**
     * Instructions - event handlers. Includes all event handlers (rules)
     * defined in this context.
     */
    public Instructions instructions = new Instructions();

    /**
     * Expressions, such as All<T>, defined in this context.
     */
    public Expressions expressions = new Expressions();

    /* Event handler methods */

    public Effect<T> on(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new On<T>(action));
    }

    public Effect<T> on(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action");
        return add(new On<T>(action, description));
    }

    public Effect<T> on(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new On<T>(condition, action));
    }

    public Effect<T> on(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new On<T>(condition, action));
    }

    public Effect<T> create(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnCreate<T>(action));
    }

    public Effect<T> create(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        return add(new OnCreate<T>(action, description));
    }

    public Effect<T> create(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnCreate<T>(condition, action));
    }

    public Effect<T> create(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnCreate<T>(condition, action, description));
    }

    public Effect<T> change(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnChange<T>(action));
    }

    public Effect<T> change(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        return add(new OnChange<T>(action, description));
    }

    public Effect<T> change(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnChange<T>(condition, action));
    }

    public Effect<T> change(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnChange<T>(condition, action, description));
    }

    public Effect<T> delete(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnDelete<T>(action));
    }

    public Effect<T> delete(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        return add(new OnDelete<T>(action, description));
    }

    public Effect<T> delete(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnDelete<T>(condition, action));
    }

    public Effect<T> delete(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnDelete<T>(condition, action, description));
    }

    public Effect<T> modify(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnModify<T>(action));
    }

    public Effect<T> modify(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        return add(new OnModify<T>(action, description));
    }

    public Effect<T> modify(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnModify<T>(condition, action));
    }

    public Effect<T> modify(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnModify<T>(condition, action, description));
    }

    public Effect<T> mutate(BiConsumer<? super T, ? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnMutate<T>(action));
    }

    public Effect<T> mutate(BiConsumer<? super T, ? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        return add(new OnMutate<T>(action, description));
    }

    public Effect<T> mutate(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnMutate<T>(condition, action));
    }

    public Effect<T> mutate(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnMutate<T>(condition, action, description));
    }

    public Effect<T> mutate(BiPredicate<? super T, ? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnMutate<T>(condition, action));
    }

    public Effect<T> mutate(BiPredicate<? super T, ? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnMutate<T>(condition, action, description));
    }

    public Effect<T> read(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnRead<T>(action));
    }

    public Effect<T> read(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        return add(new OnRead<T>(action, description));
    }

    public Effect<T> read(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnRead<T>(condition, action));
    }

    public Effect<T> read(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnRead<T>(condition, action, description));
    }

    public Effect<T> readAndLock(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnReadAndLock<T>(action));
    }

    public Effect<T> readAndLock(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        return add(new OnReadAndLock<T>(action, description));
    }

    public Effect<T> readAndLock(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnReadAndLock<T>(condition, action));
    }

    public Effect<T> readAndLock(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnReadAndLock<T>(condition, action, description));
    }

    public Effect<T> write(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnWrite<T>(action));
    }

    public Effect<T> write(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        return add(new OnWrite<T>(action, description));
    }

    public Effect<T> write(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnWrite<T>(condition, action));
    }

    public Effect<T> write(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnWrite<T>(condition, action, description));
    }

    public Effect<T> transit(BiConsumer<? super T, ? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnTransit<T>(action));
    }

    public Effect<T> transit(BiConsumer<? super T, ? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        return add(new OnTransit<T>(action, description));
    }

    public Effect<T> transit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnTransit<T>(condition, action));
    }

    public Effect<T> transit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnTransit<T>(condition, action, description));
    }

    public Effect<T> transit(BiPredicate<? super T, ? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnTransit<T>(condition, action));
    }

    public Effect<T> transit(BiPredicate<? super T, ? super T> condition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnTransit<T>(condition, action, description));
    }

    public Effect<T> update(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnUpdate<T>(action));
    }

    public Effect<T> update(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        return add(new OnUpdate<T>(action, description));
    }

    public Effect<T> update(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnUpdate<T>(condition, action));
    }

    public Effect<T> update(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnUpdate<T>(condition, action, description));
    }

    public Effect<T> upsert(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        return add(new OnUpsert<T>(action));
    }

    public Effect<T> upsert(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        return add(new OnUpsert<T>(action, description));
    }

    public Effect<T> upsert(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        return add(new OnUpsert<T>(condition, action));
    }

    public Effect<T> upsert(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        return add(new OnUpsert<T>(condition, action, description));
    }

    /* Add event handlers and expressions */

    protected Effect<T> add(EventHandler<T> eh)
    {
        Args.cool(eh, "eh");
        instructions.add(eh);
        return this;
    }

    public Effect<T> add(Expression<T> expr)
    {
        Args.cool(expr, "expr");
        expressions.add(expr);
        return this;
    }

    /* Other methods */

    public Transaction getTransaction()
    {
        State.cool(tran, "tran");
        return tran;
    }

    public void setTransaction(Transaction tr)
    {
        Args.cool(tr, "tr");
        tran = tr;
    }
}
