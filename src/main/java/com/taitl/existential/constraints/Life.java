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
 * Declares handlers for transaction lifecycle events, such as Begin, Commit, Rollback.
 */
public class Life<T extends Transaction> implements Evs<T>, Immediate<T>, SideEffects<T>
{
    /**
     * Parent Transaction object, if any.
     * This field is null for lives that are not declared on
     * transaction level, e.g. declared in a context.
     */
    Transaction tran;

    /**
     * Transaction type key.
     */
    TypeKey<T> typeKey;

    /**
     * Transaction event handlers
     */
    List<Ev<T>> evs = new ArrayList<>();

    /**
     * Creates transaction lifecycle handlers for the type inferred from an anonymous subclass.
     */
    public Life()
    {
        this.typeKey = inferTypeKeyFromAnonymousSubclass(getClass(), Life.class, "Life");
    }

    /**
     * Creates transaction lifecycle handlers for the provided transaction type key.
     *
     * @param typeKey Transaction type key
     */
    public Life(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        this.typeKey = typeKey;
    }

    /**
     * Creates transaction lifecycle handlers for the provided transaction class.
     *
     * @param typeClass Transaction class
     */
    public Life(Class<T> typeClass)
    {
        sane(typeClass, "typeClass");
        this.typeKey = new TypeKey<>(typeClass);
    }

    /* Event handler methods */

    /**
     * Creates a handler for OnBegin transaction event.
     *
     * @param action Action to invoke on transaction begin
     * @return 'This' for chaining
     */
    public Life<T> begin(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnBegin<T>(null, action));
    }

    /**
     * Creates a handler for OnBegin transaction event.
     *
     * @param action Action to invoke on transaction begin
     * @param description Description of the handler
     * @return 'This' for chaining
     */
    public Life<T> begin(Consumer<? super T> action, String description)
    {
        sane(action, "action");
        return add(new OnBegin<T>(action, description));
    }

    /**
     * Creates a handler for OnBegin transaction event.
     *
     * @param condition Predicate deciding whether the handler runs
     * @param action Action to invoke on transaction begin
     * @return 'This' for chaining
     */
    public Life<T> begin(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnBegin<T>(condition, action));
    }

    /**
     * Creates a handler for OnBegin transaction event.
     *
     * @param condition Predicate deciding whether the handler runs
     * @param action Action to invoke on transaction begin
     * @param description Description of the handler
     * @return 'This' for chaining
     */
    public Life<T> begin(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnBegin<T>(condition, action, description));
    }

    /**
     * Creates a handler for OnCommit transaction event.
     *
     * @param action Action to invoke on transaction commit
     * @return This life for chaining
     */
    public Life<T> commit(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnCommit<T>(null, action));
    }

    /**
     * Creates a handler for OnCommit transaction event.
     *
     * @param action Action to invoke on transaction commit
     * @param description Description of the handler
     * @return This life for chaining
     */
    public Life<T> commit(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnCommit<T>(action, description));
    }

    /**
     * Creates a handler for OnCommit transaction event.
     *
     * @param condition Predicate deciding whether the handler runs
     * @param action Action to invoke on transaction commit
     * @return This life for chaining
     */
    public Life<T> commit(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnCommit<T>(condition, action));
    }

    /**
     * Creates a handler for OnCommit transaction event.
     *
     * @param condition Predicate deciding whether the handler runs
     * @param action Action to invoke on transaction commit
     * @param description Description of the handler
     * @return This life for chaining
     */
    public Life<T> commit(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnCommit<T>(condition, action, description));
    }

    /**
     * Creates a handler for OnCheckpoint transaction event.
     *
     * @param action Action to invoke on transaction checkpoint
     * @return This life for chaining
     */
    public Life<T> checkpoint(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnCheckpoint<T>(null, action));
    }

    /**
     * Creates a handler for OnCheckpoint transaction event.
     *
     * @param action Action to invoke on transaction checkpoint
     * @param description Description of the handler
     * @return This life for chaining
     */
    public Life<T> checkpoint(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnCheckpoint<T>(action, description));
    }

    /**
     * Creates a handler for OnCheckpoint transaction event.
     *
     * @param condition Predicate deciding whether the handler runs
     * @param action Action to invoke on transaction checkpoint
     * @return This life for chaining
     */
    public Life<T> checkpoint(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnCheckpoint<T>(condition, action));
    }

    /**
     * Creates a handler for OnCheckpoint transaction event.
     *
     * @param condition Predicate deciding whether the handler runs
     * @param action Action to invoke on transaction checkpoint
     * @param description Description of the handler
     * @return This life for chaining
     */
    public Life<T> checkpoint(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnCheckpoint<T>(condition, action, description));
    }

    /**
     * Creates a handler for OnRollback transaction event.
     *
     * @param action Action to invoke on transaction rollback
     * @return This life for chaining
     */
    public Life<T> rollback(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnRollback<T>(null, action));
    }

    /**
     * Creates a handler for OnRollback transaction event.
     *
     * @param action Action to invoke on transaction rollback
     * @param description Description of the handler
     * @return This life for chaining
     */
    public Life<T> rollback(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnRollback<T>(action, description));
    }

    /**
     * Creates a handler for OnRollback transaction event.
     *
     * @param condition Predicate deciding whether the handler runs
     * @param action Action to invoke on transaction rollback
     * @return This life for chaining
     */
    public Life<T> rollback(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnRollback<T>(condition, action));
    }

    /**
     * Creates a handler for OnRollback transaction event.
     *
     * @param condition Predicate deciding whether the handler runs
     * @param action Action to invoke on transaction rollback
     * @param description Description of the handler
     * @return This life for chaining
     */
    public Life<T> rollback(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnRollback<T>(condition, action, description));
    }

    /* Evs implementation */

    /**
     * Adds an event handler to this transaction life.
     *
     * @param ev
     *            Event handler to register
     * @return This life for chaining
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

    /* Attributes */

    /**
     * Returns the transaction associated with this life.
     *
     * @return Parent transaction
     */
    public Transaction transaction()
    {
        State.cool(tran, "tran");
        return tran;
    }

    public Transaction transactionOrNull()
    {
        return tran;
    }

    /**
     * Associates this life with a transaction.
     *
     * @param tr
     *            Transaction owning this life
     */
    public void transaction(Transaction tr)
    {
        sane(tr, "tr");
        tran = tr;
    }

    /**
     * Returns the transaction type key this life applies to.
     *
     * @return Transaction type key
     */
    public TypeKey<T> typeKey()
    {
        return typeKey;
    }

    /**
     * Sets the transaction type key this life applies to.
     *
     * @param typeKey Transaction type key
     */
    public void typeKey(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        this.typeKey = typeKey;
    }
}
