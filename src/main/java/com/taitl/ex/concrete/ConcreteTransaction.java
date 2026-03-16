package com.taitl.ex.concrete;

import com.taitl.ex.core.indexes.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.transaction_handlers.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.indexes.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Concrete state and behavior behind {@link Transaction}.
 */
public class ConcreteTransaction
{
    protected RuleData ruleData;
    protected StageName stageCursor;
    protected TransactionIndexes indexes;

    public <K, V> MultiIndex<K, V> index(String name, Function<V, K> getKey)
    {
        sane(name, "name", getKey, "getKey");
        return indexes.getOrCreate(name, getKey);
    }

    public <T> void invariant(Transaction transaction, Invariant<T> invariant)
    {
        sane(transaction, "transaction", invariant, "invariant");
        invariant(transaction, invariant, resolvedStage(invariant));
    }

    public <T> void invariant(Transaction transaction, Invariant<T> invariant, StageName stageName)
    {
        sane(transaction, "transaction", invariant, "invariant", stageName, "stageName");
        if (!invariant.hasTransaction())
        {
            invariant.transaction(transaction);
        }
        else
        {
            Transaction tr = invariant.transaction();
            check(tr == transaction, "Argument 'invariant' must belong to this transaction. " +
                    "Create it here or call invariant.transaction(this).");
        }
        add(invariant, stageName);
    }

    public <T> void effect(Transaction transaction, Effect<T> effect)
    {
        sane(transaction, "transaction", effect, "effect");
        effect(transaction, effect, resolvedStage(effect));
    }

    public <T> void effect(Transaction transaction, Effect<T> effect, StageName stageName)
    {
        sane(transaction, "transaction", effect, "effect", stageName, "stageName");
        if (!effect.hasTransaction())
        {
            effect.setTransaction(transaction);
        }
        else
        {
            Transaction tr = effect.getTransaction();
            check(tr == transaction, "Argument 'effect' must belong to this transaction. " +
                    "Create it here or call effect.setTransaction(this).");
        }
        add(effect, stageName);
    }

    public <T> void intent(Transaction transaction, Intent<T> intent)
    {
        sane(transaction, "transaction", intent, "intent");
        intent(transaction, intent, resolvedStage(intent));
    }

    public <T> void intent(Transaction transaction, Intent<T> intent, StageName stageName)
    {
        sane(transaction, "transaction", intent, "intent", stageName, "stageName");
        if (!intent.hasTransaction())
        {
            intent.transaction(transaction);
        }
        else
        {
            Transaction tr = intent.transaction();
            check(tr == transaction, "Argument 'intent' must belong to this transaction. " +
                    "Create it here or call intent.transaction(this).");
        }
        add(intent, stageName);
    }

    public <T extends Transaction> void cycle(Transaction transaction, Life<T> cycle)
    {
        sane(transaction, "transaction", cycle, "cycle");
        if (stageCursor != null)
        {
            cycle(transaction, cycle, stageCursor);
            return;
        }
        EnumSet<StageName> lifecycleStages = lifecycleStages(cycle);
        for (StageName stageName : lifecycleStages)
        {
            cycle(transaction, cycle, stageName);
        }
    }

    public <T extends Transaction> void cycle(Transaction transaction, Life<T> cycle, StageName stageName)
    {
        sane(transaction, "transaction", cycle, "cycle", stageName, "stageName");
        if (!cycle.hasTransaction())
        {
            cycle.transaction(transaction);
        }
        else
        {
            Transaction tr = cycle.transaction();
            check(tr == transaction, "Argument 'cycle' must belong to same transaction");
        }
        add(cycle, stageName);
    }

    public <T extends Transaction> void begin(Transaction transaction, Consumer<? super T> action)
    {
        life(transaction, action, StageName.BEGIN, cycle -> cycle.begin(action));
    }

    public <T extends Transaction> void commit(Transaction transaction, Consumer<? super T> action)
    {
        life(transaction, action, StageName.COMMIT, cycle -> cycle.commit(action));
    }

    public <T extends Transaction> void rollback(Transaction transaction, Consumer<? super T> action)
    {
        life(transaction, action, StageName.ROLLBACK, cycle -> cycle.rollback(action));
    }

    public <T extends Transaction> void checkpoint(Transaction transaction, Consumer<? super T> action)
    {
        life(transaction, action, StageName.CHECKPOINT, cycle -> cycle.checkpoint(action));
    }

    public <T> void add(Transaction transaction, Evs<T> evs)
    {
        sane(transaction, "transaction", evs, "evs");
        add(evs, resolvedStage(evs));
    }

    public <T> void add(Evs<T> evs, StageName stageName)
    {
        sane(evs, "evs", stageName, "stageName");
        ruleData.add(stageName, evs);
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

    public List<Evs<?>> evs()
    {
        return ruleData.all();
    }

    public RuleData stage()
    {
        return ruleData;
    }

    public void validate(Transaction transaction)
    {
        sane(transaction, "transaction");
        verify(transaction.context != null, "Transaction context is not set, call context(str) first");
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
        if (evs instanceof Life<?>)
        {
            return StageName.BEGIN;
        }
        return StageName.VALIDATION;
    }

    protected StageName lifecycleStage(EventHandler<?> handler)
    {
        sane(handler, "handler");
        StageName stageName = lifecycleStageByHandler().get(handler.getClass());
        if (stageName != null)
        {
            return stageName;
        }
        for (Map.Entry<Class<?>, StageName> entry : lifecycleStageByHandler().entrySet())
        {
            if (entry.getKey().isAssignableFrom(handler.getClass()))
            {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("Unsupported lifecycle handler type: " + handler.getClass().getName());
    }

    protected <T extends Transaction> EnumSet<StageName> lifecycleStages(Life<T> cycle)
    {
        sane(cycle, "cycle");
        EnumSet<StageName> stages = EnumSet.noneOf(StageName.class);
        for (Ev<T> ev : cycle.list())
        {
            if (!(ev instanceof EventHandler<?>))
            {
                throw new IllegalArgumentException(
                        "Lifecycle rule must be an EventHandler: " + ev.getClass().getName());
            }
            stages.add(lifecycleStage((EventHandler<?>) ev));
        }
        if (stages.isEmpty())
        {
            throw new IllegalArgumentException("Lifecycle rule set must contain at least one handler");
        }
        return stages;
    }

    protected <T extends Transaction> void life(
            Transaction transaction,
            Consumer<? super T> action,
            StageName stageName,
            Consumer<Life<T>> registrar)
    {
        sane(transaction, "transaction", action, "action", stageName, "stageName", registrar, "registrar");
        Life<T> cycle = new Life<>(transactionTypeKey());
        registrar.accept(cycle);
        cycle(transaction, cycle, stageName);
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Transaction> com.taitl.existential.keys.TypeKey<T> transactionTypeKey()
    {
        return (com.taitl.existential.keys.TypeKey<T>) new com.taitl.existential.keys.TypeKey<Transaction>(
                Transaction.class);
    }

    protected static Map<Class<?>, StageName> lifecycleStageByHandler()
    {
        Map<Class<?>, StageName> map = new LinkedHashMap<>();
        map.put(OnBegin.class, StageName.BEGIN);
        map.put(OnCommit.class, StageName.COMMIT);
        map.put(OnCheckpoint.class, StageName.CHECKPOINT);
        map.put(OnRollback.class, StageName.ROLLBACK);
        return Collections.unmodifiableMap(map);
    }
}
