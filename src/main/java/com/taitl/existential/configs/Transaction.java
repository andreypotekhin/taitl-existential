package com.taitl.existential.configs;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.managed_indexes.data.*;
import com.taitl.existential.*;
import com.taitl.existential.indexes.*;
import com.taitl.ex.concrete.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.interfaces.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Implements constraints and effects at transaction level.
 *
 * "Transactions" in this library are not database transactions, but rather
 * they are markers of the beginning and end of business operations
 * (a web request, batch job, etc.).
 *
 * At transaction end (a commit), rules such as All and Exists expressions,
 * defined for the current Context and its parent Contexts, are evaluated.
 *
 * A Transaction instance can store data to pass between event handlers.
 *
 * @see Context
 * @see com.taitl.ex.core.indexes.TransactionIndexes
 * @see com.taitl.existential.builders.TransactionBuilder
 */
public class Transaction implements Configurable, Evaluable
{
    public static BiFunction<String, String, ? extends Transaction> FACTORY = (op, name) -> new Transaction(op, name);

    protected final ConcreteTransaction concrete;
    private final IndexConfiguration indexConfiguration = new IndexConfiguration();
    public final UUID id;
    public String op;
    public String name;
    public Context context;

    public Transaction(String op, String name)
    {
        sane(op, "op", name, "name");
        this.op = op;
        this.name = name;
        this.id = generateId();
        this.concrete = createBuilder()
                .transaction(this)
                .build();
    }

    public <K, V> MultiIndex<K, V> index(String name, Function<V, K> getKey)
    {
        return concrete.index(name, getKey);
    }

    /**
     * Registers a typed managed index declaration for this transaction.
     *
     * @param <I> index facade type
     * @param definition registered immutable definition token
     * @return this scope for further configuration
     */
    public <I> Transaction index(IndexDefinition<I> definition)
    {
        indexConfiguration.add(definition);
        return this;
    }

    /** Sets independent managed-index defaults for this scope before binding. */
    public Transaction indexingPolicy(IndexingPolicy policy)
    {
        indexConfiguration.policy(policy);
        return this;
    }

    /** Returns this scope's inheritable managed-index overrides. */
    public IndexingPolicy indexingPolicy()
    {
        return indexConfiguration.policy();
    }

    /** Returns registered immutable tokens; their mutable contents belong to active handles. */
    public List<IndexDefinition<?>> indexes()
    {
        return indexConfiguration.definitions();
    }

    /** Freezes managed declarations when this scope is bound to an active handle. */
    public void freezeIndexes()
    {
        indexConfiguration.freeze();
    }

    public <T> void invariant(Invariant<T> invariant)
    {
        concrete.invariant(this, invariant);
    }

    public <T> void invariant(Invariant<T> invariant, StageName stageName)
    {
        concrete.invariant(this, invariant, stageName);
    }

    public <T> void effect(Effect<T> effect)
    {
        concrete.effect(this, effect);
    }

    public <T> void effect(Effect<T> effect, StageName stageName)
    {
        concrete.effect(this, effect, stageName);
    }

    public <T> void intent(Intent<T> intent)
    {
        concrete.intent(this, intent);
    }

    public <T> void intent(Intent<T> intent, StageName stageName)
    {
        concrete.intent(this, intent, stageName);
    }

    public <T extends Transaction> void cycle(Life<T> cycle)
    {
        concrete.cycle(this, cycle);
    }

    public <T extends Transaction> void cycle(Life<T> cycle, StageName stageName)
    {
        concrete.cycle(this, cycle, stageName);
    }

    public <T extends Transaction> void begin(Consumer<? super T> action)
    {
        concrete.begin(this, action);
    }

    public <T extends Transaction> void commit(Consumer<? super T> action)
    {
        concrete.commit(this, action);
    }

    public <T extends Transaction> void rollback(Consumer<? super T> action)
    {
        concrete.rollback(this, action);
    }

    public <T extends Transaction> void checkpoint(Consumer<? super T> action)
    {
        concrete.checkpoint(this, action);
    }

    public <T> void add(Evs<T> evs)
    {
        concrete.add(this, evs);
    }

    public <T> void add(Evs<T> evs, StageName stageName)
    {
        concrete.add(evs, stageName);
    }

    public Transaction begin()
    {
        concrete.begin();
        return this;
    }

    public Transaction immediate()
    {
        concrete.immediate();
        return this;
    }

    public Transaction validation()
    {
        concrete.validation();
        return this;
    }

    public Transaction commit()
    {
        concrete.commit();
        return this;
    }

    public Transaction checkpoint()
    {
        concrete.checkpoint();
        return this;
    }

    public Transaction rollback()
    {
        concrete.rollback();
        return this;
    }

    public List<Evs<?>> evs()
    {
        return concrete.evs();
    }

    public RuleData stage()
    {
        return concrete.stage();
    }

    public void op(String op)
    {
        this.op = op;
    }

    public void name(String name)
    {
        this.name = name;
    }

    protected UUID generateId()
    {
        return UUID.randomUUID();
    }

    public Context context()
    {
        return context;
    }

    public void context(Context context)
    {
        sane(context, "context");
        this.context = context;
    }

    public void validate()
    {
        concrete.validate(this);
    }

    protected ConcreteTransactionBuilder createBuilder()
    {
        return Creator.create(ConcreteTransactionBuilder.class);
    }
}
