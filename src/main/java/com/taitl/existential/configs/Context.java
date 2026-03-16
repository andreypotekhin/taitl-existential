package com.taitl.existential.configs;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.concrete.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.interfaces.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Describes a business context within a configuration, including invariants,
 * effects, and event handlers that should apply to a specific operation.
 *
 * Contexts can form a hierarchy, where parent rules execute before child rules,
 * and can be customized with transaction and event-splitting factories.
 *
 * @see Config
 * @see Transaction
 */
public class Context implements Configurable, Evaluable
{
    public static Supplier<? extends Context> FACTORY = () -> Creator.create(Context.class);

    protected final ConcreteContext concrete;
    protected String name;
    protected Context parent;

    public Context(String name)
    {
        sane(name, "name");
        this.name = name;
        this.concrete = createBuilder().build();
    }

    public Context(String name, Context parent)
    {
        sane(name, "name", parent, "parent");
        this.name = name;
        this.parent = parent;
        this.concrete = createBuilder()
                .inheritedTransactionFactory()
                .build();
    }

    public <T> void invariant(Invariant<T> invariant)
    {
        concrete.invariant(this, invariant);
    }

    public <T> void effect(Effect<T> effect)
    {
        concrete.effect(this, effect);
    }

    public <T> void intent(Intent<T> intent)
    {
        concrete.intent(this, intent);
    }

    public Context begin()
    {
        concrete.begin();
        return this;
    }

    public Context immediate()
    {
        concrete.immediate();
        return this;
    }

    public Context validation()
    {
        concrete.validation();
        return this;
    }

    public Context commit()
    {
        concrete.commit();
        return this;
    }

    public Context checkpoint()
    {
        concrete.checkpoint();
        return this;
    }

    public Context rollback()
    {
        concrete.rollback();
        return this;
    }

    public Context transaction(Supplier<? extends Transaction> supplier)
    {
        concrete.transaction(supplier);
        return this;
    }

    public Context transaction(BiFunction<String, String, ? extends Transaction> factory)
    {
        concrete.transaction(factory);
        return this;
    }

    public <T> void add(Evs<T> evs)
    {
        concrete.add(this, evs);
    }

    public <T> void add(Evs<T> evs, StageName stageName)
    {
        concrete.add(evs, stageName);
    }

    public Context addAll(Context other)
    {
        concrete.addAll(other);
        return this;
    }

    public List<Evs<?>> evs()
    {
        return concrete.evs();
    }

    public Stages stage()
    {
        return concrete.stage();
    }

    public boolean hasParent()
    {
        return parent != null;
    }

    public Context parent()
    {
        return parent;
    }

    public void parent(Context parent)
    {
        this.parent = parent;
    }

    public String name()
    {
        return name;
    }

    public void op(String op)
    {
        this.name = op;
    }

    public BiFunction<String, String, ? extends Transaction> transactionFactory()
    {
        return concrete.transactionFactory(this);
    }

    protected ConcreteContextBuilder createBuilder()
    {
        return Creator.create(ConcreteContextBuilder.class);
    }
}
