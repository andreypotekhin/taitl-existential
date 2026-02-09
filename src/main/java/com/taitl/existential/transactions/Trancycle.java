package com.taitl.existential.transactions;

import java.util.*;
import java.util.function.*;
import com.taitl.ex.common.helper.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.transaction_handlers.*;
import com.taitl.existential.interfaces.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Holds handlers for transaction lifecycle events, such as Begin, Commit, Rollback.
 */
public class Trancycle<T extends Transaction> implements Evs<T>, Immediate<T>
{
    /**
     * Parent Transaction object, if any.
     * This field is null for trancycles that are not declared on
     * transaction level, e.g. declared in a context.
     */
    Transaction tran;

    /**
     * Transaction event handlers
     */
    List<Ev<T>> evs = new ArrayList<>();

    /* Event handler methods */

    public Trancycle<T> begin(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnBegin<T>(action));
    }

    public Trancycle<T> begin(Consumer<? super T> action, String description)
    {
        sane(action, "action");
        return add(new OnBegin<T>(action, description));
    }

    public Trancycle<T> begin(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnBegin<T>(condition, action));
    }

    public Trancycle<T> begin(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnBegin<T>(condition, action));
    }

    public Trancycle<T> commit(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnCommit<T>(action));
    }

    public Trancycle<T> commit(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnCommit<T>(action, description));
    }

    public Trancycle<T> commit(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnCommit<T>(condition, action));
    }

    public Trancycle<T> commit(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnCommit<T>(condition, action, description));
    }

    public Trancycle<T> rollback(Consumer<? super T> action)
    {
        sane(action, "action");
        return add(new OnRollback<T>(action));
    }

    public Trancycle<T> rollback(Consumer<? super T> action, String description)
    {
        sane(action, "action", description, "description");
        return add(new OnRollback<T>(action, description));
    }

    public Trancycle<T> rollback(Predicate<? super T> condition, Consumer<? super T> action)
    {
        sane(condition, "condition", action, "action");
        return add(new OnRollback<T>(condition, action));
    }

    public Trancycle<T> rollback(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        return add(new OnRollback<T>(condition, action, description));
    }

    /* Add event handlers and expressions */

    protected Trancycle<T> add(Ev<T> ev)
    {
        sane(ev, "eh");
        evs.add(ev);
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
        sane(tr, "tr");
        tran = tr;
    }

    public List<Ev<T>> list()
    {
        return evs;
    }
}
