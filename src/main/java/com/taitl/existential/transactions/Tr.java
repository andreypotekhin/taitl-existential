package com.taitl.existential.transactions;

import com.taitl.ex.concrete.*;
import com.taitl.ex.logic.managed_indexes.*;
import com.taitl.existential.indexes.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.events.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.indexing.data.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;

import java.util.*;

/**
 * Defines an existential transaction, the backbone of the library transaction model.
 * Holds Transaction objects (rule configurations) that apply to a single business operation.
 * Multiple Contexts can match an operation (parent-child contexts and wildcard contexts),
 * and this class keeps the Transaction objects created by each matching Context so their
 * rules can be accessed.
 * The Transaction order follows the declaration order of their Contexts: parent contexts
 * before child contexts, and wildcard contexts before specific contexts.
 */
public class Tr
{
    public static final String TROUBLESHOOTING_SECTION = ConcreteTr.TROUBLESHOOTING_SECTION;

    public final UUID id;
    public String op;
    protected final ConcreteTr concrete;

    public Tr(String op, UUID id, TransactionLogic tl, EventLogic el)
    {
        this.id = id;
        this.op = op;
        this.concrete = createBuilder()
                .tr(this)
                .op(op)
                .id(id)
                .tl(tl)
                .el(el)
                .build();
    }

    /**
     * Resolves a registered immutable token to this handle's independently owned index facade.
     *
     * @param <I> index facade type
     * @param definition registered immutable definition token
     * @return this handle's bound facade
     */
    public <I> I index(IndexDefinition<I> definition)
    {
        if (concrete.indexMaintenance() == null)
        {
            throw new IllegalArgumentException("Register a managed index definition before begin().");
        }
        return concrete.indexMaintenance().index(definition);
    }

    /** Binds managed definitions before begin callbacks; ordinary transactions allocate no index state. */
    public void bindManagedIndexes()
    {
        concrete.bindManagedIndexes();
    }

    /** Supplies the transaction-owned maintenance delegate to event and boundary integration. */
    public IndexMaintenanceLogic indexMaintenance()
    {
        return concrete.indexMaintenance();
    }

    public void addTransaction(Transaction tr)
    {
        concrete.addTransaction(tr);
    }

    /**
     * Validates the events reported so far while keeping the handle active after success. A
     * failed checkpoint makes the handle rollback-only; explicitly call {@link #rollback()} or
     * {@link #close()} before starting replacement work.
     */
    public void checkpoint()
    {
        concrete.checkpoint();
    }

    public void commit()
    {
        concrete.commit();
    }

    public void rollback()
    {
        concrete.rollback();
    }

    public <T> void event(Event<T> event, T t, TypeKey<T> type)
    {
        concrete.event(event, t, type);
    }

    public <T> void event(BiEvent<T> event, TypeKey<T> type)
    {
        concrete.event(event, type);
    }

    public <T> void create(T t, TypeKey<T> type)
    {
        concrete.create(t, type);
    }

    public <T> void create(T t)
    {
        concrete.create(t);
    }

    public <T> void delete(T t, TypeKey<T> type)
    {
        concrete.delete(t, type);
    }

    public <T> void delete(T t)
    {
        concrete.delete(t);
    }

    public <T> void update(T t, TypeKey<T> type)
    {
        concrete.update(t, type);
    }

    public <T> void update(T t)
    {
        concrete.update(t);
    }

    public <T> void transit(T t0, T t1, TypeKey<T> type)
    {
        concrete.transit(t0, t1, type);
    }

    public <T> void transit(T t0, T t1)
    {
        concrete.transit(t0, t1);
    }

    public <T> void port(T t0, T t1, TypeKey<T> type)
    {
        concrete.port(t0, t1, type);
    }

    public <T> void port(T t0, T t1)
    {
        concrete.port(t0, t1);
    }

    public <T> void read(T entity, TypeKey<T> type)
    {
        concrete.read(entity, type);
    }

    public <T> void read(T entity)
    {
        concrete.read(entity);
    }

    public <T> void memo(T before, T live, TypeKey<T> typeKey)
    {
        concrete.memo(before, live, typeKey);
    }

    public <T> void memo(T before, T live, Class<T> cls)
    {
        concrete.memo(before, live, cls);
    }

    public <T> T beforeState(T live, TypeKey<T> typeKey)
    {
        return concrete.beforeState(live, typeKey);
    }

    public <T> boolean hasMemo(T live, TypeKey<T> typeKey)
    {
        return concrete.hasMemo(live, typeKey);
    }

    public <T> void write(T entity, TypeKey<T> type)
    {
        concrete.write(entity, type);
    }

    public <T> void write(T entity)
    {
        concrete.write(entity);
    }

    public void onBegin()
    {
        concrete.onBegin();
    }

    public void onCheckpoint()
    {
        concrete.onCheckpoint();
    }

    public void onCommit()
    {
        concrete.onCommit();
    }

    public void onRollback()
    {
        concrete.onRollback();
    }

    public void preparedIndexes(StageName stageName, ConfigurationIndexes indexes)
    {
        concrete.preparedIndexes(stageName, indexes);
    }

    public EventField eventField(EventField base, StageName stageName)
    {
        return concrete.eventField(stageName, base);
    }

    public String id()
    {
        return concrete.id();
    }

    public List<Transaction> transactions()
    {
        return concrete.transactions();
    }

    public IndexData runtimeIndexes()
    {
        return concrete.runtimeIndexes();
    }

    public boolean hasIntents()
    {
        return concrete.hasIntents();
    }

    public boolean hasIntentEventType(EventType eventType)
    {
        return concrete.hasIntentEventType(eventType);
    }

    public boolean hasIntents(StageName stageName)
    {
        return concrete.hasIntents(stageName);
    }

    public boolean hasIntentEventType(StageName stageName, EventType eventType)
    {
        return concrete.hasIntentEventType(stageName, eventType);
    }

    public boolean hasBiIntentHandler(StageName stageName, EventType eventType, String typeKey)
    {
        return concrete.hasBiIntentHandler(stageName, eventType, typeKey);
    }

    public List<EventHandler<?>> intentHandlers(EventType eventType, String typeKey)
    {
        return concrete.intentHandlers(eventType, typeKey);
    }

    public List<EventHandler<?>> intentHandlers(StageName stageName, EventType eventType, String typeKey)
    {
        return concrete.intentHandlers(stageName, eventType, typeKey);
    }

    public boolean beginActive()
    {
        return concrete.beginActive();
    }

    public boolean immediateActive()
    {
        return concrete.immediateActive();
    }

    public boolean hasPreparedHandlers(StageName stageName)
    {
        return concrete.hasPreparedHandlers(stageName);
    }

    public void activateReportStages(boolean begin, boolean immediate)
    {
        concrete.activateReportStages(begin, immediate);
    }

    public <T> boolean shouldEvaluateBegin(RuntimeKey<T> runtimeKey)
    {
        return concrete.shouldEvaluateBegin(runtimeKey);
    }

    public void close()
    {
        concrete.close();
    }

    public void callbackStarted()
    {
        concrete.callbackStarted();
    }

    public void callbackFinished()
    {
        concrete.callbackFinished();
    }

    ConcreteTrBuilder createBuilder()
    {
        return Creator.create(ConcreteTrBuilder.class);
    }
}
