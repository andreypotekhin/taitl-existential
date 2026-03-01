package com.taitl.existential.constraints;

import com.taitl.ex.common.helper.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.transaction_handlers.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.lang.Generics.*;

/**
 * Holds handlers for transaction lifecycle events, such as Begin, Commit, Rollback.
 */
public class Life<T extends Transaction> implements Evs<T>, Immediate<T>, SideEffects<T>
{
    /**
     * Parent Transaction object, if any.
     * This field is null for trancycles that are not declared on
     * transaction level, e.g. declared in a context.
     */
    Transaction tran;
    TypeKey<T> typeKey;

    /**
     * Transaction event handlers
     */
    List<Ev<T>> evs = new ArrayList<>();

    public Life()
    {
        this.typeKey = inferTypeKeyFromAnonymousSubclass(getClass(), Life.class, "Trancycle");
    }

    public Life(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        this.typeKey = typeKey;
    }

    public Life(Class<T> typeClass)
    {
        sane(typeClass, "typeClass");
        this.typeKey = new TypeKey<>(typeClass);
    }

    /* Event handler methods */

    /**
     * Registers a begin handler that always executes.
     *
     * @param action
     *            Action to invoke on transaction begin
     * @return This trancycle for chaining
     */
    public Life<T> begin(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnBegin<T>(null, action));
    }

    /**
     * Registers a begin handler with a human-friendly description.
     *
     * @param action
     *            Action to invoke on transaction begin
     * @param description
     *            Description of the handler
     * @return This trancycle for chaining
     */
    public Life<T> begin(Consumer<? super T> action, String description)
    {
        sane(action, "action");
        return add(new OnBegin<T>(action, description));
    }

    /**
     * Registers a conditional begin handler.
     *
     * @param condition
     *            Predicate deciding whether the handler runs
     * @param action
     *            Action to invoke on transaction begin
     * @return This trancycle for chaining
     */
    public Life<T> begin(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnBegin<T>(condition, action));
    }

    /**
     * Registers a conditional begin handler with a description.
     *
     * @param condition
     *            Predicate deciding whether the handler runs
     * @param action
     *            Action to invoke on transaction begin
     * @param description
     *            Description of the handler
     * @return This trancycle for chaining
     */
    public Life<T> begin(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnBegin<T>(condition, action, description));
    }

    /**
     * Registers a commit handler that always executes.
     *
     * @param action
     *            Action to invoke on transaction commit
     * @return This trancycle for chaining
     */
    public Life<T> commit(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnCommit<T>(null, action));
    }

    /**
     * Registers a commit handler with a human-friendly description.
     *
     * @param action
     *            Action to invoke on transaction commit
     * @param description
     *            Description of the handler
     * @return This trancycle for chaining
     */
    public Life<T> commit(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnCommit<T>(action, description));
    }

    /**
     * Registers a conditional commit handler.
     *
     * @param condition
     *            Predicate deciding whether the handler runs
     * @param action
     *            Action to invoke on transaction commit
     * @return This trancycle for chaining
     */
    public Life<T> commit(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnCommit<T>(condition, action));
    }

    /**
     * Registers a conditional commit handler with a description.
     *
     * @param condition
     *            Predicate deciding whether the handler runs
     * @param action
     *            Action to invoke on transaction commit
     * @param description
     *            Description of the handler
     * @return This trancycle for chaining
     */
    public Life<T> commit(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnCommit<T>(condition, action, description));
    }

    /**
     * Registers a checkpoint handler that always executes.
     *
     * @param action
     *            Action to invoke on transaction checkpoint
     * @return This trancycle for chaining
     */
    public Life<T> checkpoint(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnCheckpoint<T>(null, action));
    }

    /**
     * Registers a checkpoint handler with a human-friendly description.
     *
     * @param action
     *            Action to invoke on transaction checkpoint
     * @param description
     *            Description of the handler
     * @return This trancycle for chaining
     */
    public Life<T> checkpoint(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnCheckpoint<T>(action, description));
    }

    /**
     * Registers a conditional checkpoint handler.
     *
     * @param condition
     *            Predicate deciding whether the handler runs
     * @param action
     *            Action to invoke on transaction checkpoint
     * @return This trancycle for chaining
     */
    public Life<T> checkpoint(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnCheckpoint<T>(condition, action));
    }

    /**
     * Registers a conditional checkpoint handler with a description.
     *
     * @param condition
     *            Predicate deciding whether the handler runs
     * @param action
     *            Action to invoke on transaction checkpoint
     * @param description
     *            Description of the handler
     * @return This trancycle for chaining
     */
    public Life<T> checkpoint(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnCheckpoint<T>(condition, action, description));
    }

    /**
     * Registers a rollback handler that always executes.
     *
     * @param action
     *            Action to invoke on transaction rollback
     * @return This trancycle for chaining
     */
    public Life<T> rollback(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnRollback<T>(null, action));
    }

    /**
     * Registers a rollback handler with a human-friendly description.
     *
     * @param action
     *            Action to invoke on transaction rollback
     * @param description
     *            Description of the handler
     * @return This trancycle for chaining
     */
    public Life<T> rollback(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnRollback<T>(action, description));
    }

    /**
     * Registers a conditional rollback handler.
     *
     * @param condition
     *            Predicate deciding whether the handler runs
     * @param action
     *            Action to invoke on transaction rollback
     * @return This trancycle for chaining
     */
    public Life<T> rollback(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnRollback<T>(condition, action));
    }

    /**
     * Registers a conditional rollback handler with a description.
     *
     * @param condition
     *            Predicate deciding whether the handler runs
     * @param action
     *            Action to invoke on transaction rollback
     * @param description
     *            Description of the handler
     * @return This trancycle for chaining
     */
    public Life<T> rollback(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnRollback<T>(condition, action, description));
    }

    /* Evs implementation */

    /**
     * Adds an event handler to this transaction cycle.
     *
     * @param ev
     *            Event handler to register
     * @return This trancycle for chaining
     */
    public Life<T> add(Ev<T> ev)
    {
        sane(ev, "eh");
        evs.add(ev);
        return this;
    }

    /**
     * Returns the list of registered transaction event handlers.
     *
     * @return Ordered list of handlers
     */
    public List<Ev<T>> list()
    {
        return evs;
    }

    public TypeKey<T> typeKey()
    {
        return typeKey;
    }

    /* Attributes */

    /**
     * Returns the transaction associated with this trancycle.
     *
     * @return Parent transaction
     */
    public Transaction transaction()
    {
        State.cool(tran, "tran");
        return tran;
    }

    /**
     * Associates this trancycle with a transaction.
     *
     * @param tr
     *            Transaction owning this trancycle
     */
    public void transaction(Transaction tr)
    {
        sane(tr, "tr");
        tran = tr;
    }

    public void typeKey(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        this.typeKey = typeKey;
    }
}
