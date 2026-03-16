package com.taitl.ex.concrete;

import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Concrete state and behavior behind {@link Context}.
 */
public class ConcreteContext
{
    protected Stages stages;
    protected StageName stageCursor;
    protected BiFunction<String, String, ? extends Transaction> transactionFactory;

    public <T> void invariant(Context context, Invariant<T> invariant)
    {
        sane(context, "context", invariant, "invariant");
        add(context, invariant);
    }

    public <T> void effect(Context context, Effect<T> effect)
    {
        sane(context, "context", effect, "effect");
        add(context, effect);
    }

    public <T> void intent(Context context, Intent<T> intent)
    {
        sane(context, "context", intent, "intent");
        add(context, intent);
    }

    public void begin()
    {
        stageCursor = StageName.BEGIN;
    }

    public void immediate()
    {
        stageCursor = StageName.IMMEDIATE;
    }

    public void validation()
    {
        stageCursor = StageName.VALIDATION;
    }

    public void commit()
    {
        stageCursor = StageName.COMMIT;
    }

    public void checkpoint()
    {
        stageCursor = StageName.CHECKPOINT;
    }

    public void rollback()
    {
        stageCursor = StageName.ROLLBACK;
    }

    public void transaction(Supplier<? extends Transaction> supplier)
    {
        sane(supplier, "supplier");
        transactionFactory = (op, name) -> supplier.get();
    }

    public void transaction(BiFunction<String, String, ? extends Transaction> factory)
    {
        sane(factory, "factory");
        transactionFactory = factory;
    }

    public <T> void add(Context context, Evs<T> evs)
    {
        sane(context, "context", evs, "evs");
        add(evs, resolvedStage(evs));
    }

    public <T> void add(Evs<T> evs, StageName stageName)
    {
        sane(evs, "evs", stageName, "stageName");
        stages.add(stageName, evs);
    }

    public void addAll(Context other)
    {
        sane(other, "other");
        stages.addAll(other.stage());
    }

    public List<Evs<?>> evs()
    {
        return stages.all();
    }

    public Stages stage()
    {
        return stages;
    }

    public BiFunction<String, String, ? extends Transaction> transactionFactory(Context context)
    {
        sane(context, "context");
        if (transactionFactory != null)
        {
            return transactionFactory;
        }
        return context.parent() != null ? context.parent().transactionFactory() : Transaction.FACTORY;
    }

    protected <T> StageName resolvedStage(Evs<T> evs)
    {
        sane(evs, "evs");
        if (stageCursor != null)
        {
            return stageCursor;
        }
        if (evs instanceof Intent<?>)
        {
            return StageName.IMMEDIATE;
        }
        return StageName.VALIDATION;
    }
}
