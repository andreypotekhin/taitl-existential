package com.taitl.existential.transactions;

import com.taitl.ex.concrete.*;
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

    public void addTransaction(Transaction tr)
    {
        concrete.addTransaction(tr);
    }

    public void checkpoint() throws ExistentialException
    {
        concrete.checkpoint();
    }

    public void commit() throws ExistentialException
    {
        concrete.commit();
    }

    public void rollback() throws ExistentialException
    {
        concrete.rollback();
    }

    public <T> void event(Event<T> event, T t, TypeKey<T> type) throws ExistentialException
    {
        concrete.event(event, t, type);
    }

    public <T> void event(BiEvent<T> event, TypeKey<T> type) throws ExistentialException
    {
        concrete.event(event, type);
    }

    public <T> void create(T t, TypeKey<T> type) throws ExistentialException
    {
        concrete.create(t, type);
    }

    public <T> void create(T t) throws ExistentialException
    {
        concrete.create(t);
    }

    public <T> void delete(T t, TypeKey<T> type) throws ExistentialException
    {
        concrete.delete(t, type);
    }

    public <T> void delete(T t) throws ExistentialException
    {
        concrete.delete(t);
    }

    public <T> void update(T t, TypeKey<T> type) throws ExistentialException
    {
        concrete.update(t, type);
    }

    public <T> void update(T t) throws ExistentialException
    {
        concrete.update(t);
    }

    public <T> void transit(T t0, T t1, TypeKey<T> type) throws ExistentialException
    {
        concrete.transit(t0, t1, type);
    }

    public <T> void transit(T t0, T t1) throws ExistentialException
    {
        concrete.transit(t0, t1);
    }

    public <T> void port(T t0, T t1, TypeKey<T> type) throws ExistentialException
    {
        concrete.port(t0, t1, type);
    }

    public <T> void port(T t0, T t1) throws ExistentialException
    {
        concrete.port(t0, t1);
    }

    public <T> void read(T entity, TypeKey<T> type) throws ExistentialException
    {
        concrete.read(entity, type);
    }

    public <T> void read(T entity) throws ExistentialException
    {
        concrete.read(entity);
    }

    public <T> void memo(T before, T live, TypeKey<T> typeKey) throws ExistentialException
    {
        concrete.memo(before, live, typeKey);
    }

    public <T> void memo(T before, T live, Class<T> cls) throws ExistentialException
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

    public <T> void write(T entity, TypeKey<T> type) throws ExistentialException
    {
        concrete.write(entity, type);
    }

    public <T> void write(T entity) throws ExistentialException
    {
        concrete.write(entity);
    }

    public void onBegin() throws ExistentialException
    {
        concrete.onBegin();
    }

    public void onCheckpoint() throws ExistentialException
    {
        concrete.onCheckpoint();
    }

    public void onCommit() throws ExistentialException
    {
        concrete.onCommit();
    }

    public void onRollback() throws ExistentialException
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

    public <T> boolean shouldEvaluateBegin(RuntimeKey<T> runtimeKey)
    {
        return concrete.shouldEvaluateBegin(runtimeKey);
    }

    public void close()
    {
        concrete.close();
    }

    ConcreteTrBuilder createBuilder()
    {
        return Creator.create(ConcreteTrBuilder.class);
    }
}
