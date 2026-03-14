package com.taitl.ex.concrete;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.events.*;
import com.taitl.ex.logic.indexing.data.*;
import com.taitl.ex.logic.stages.validation.data.*;
import com.taitl.ex.logic.tr.*;
import com.taitl.ex.logic.tr.actions.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.ex.logic.transactions.data.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.transaction_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Concrete implementation of an existential transaction.
 */
public class ConcreteTr
{
    public static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#transaction-closed";

    @Up
    protected Tr tr;

    @Up
    protected TransactionLogic tl;

    @Up
    protected EventLogic el;

    @Logic
    protected IndexData runtimeIndexes;

    @Logic
    protected ValidationData validationData;

    @Logic
    protected TrMemos memos;

    @Logic
    protected Map<StageName, StageData> stageData = new EnumMap<>(StageName.class);

    @Logic
    protected IntentLogic intentLogic;

    @Logic
    protected IndexIntents indexIntents;

    @Logic
    protected ExecuteLifecycle executeLifecycle;

    public final UUID id;
    public String op;
    protected List<Transaction> transactions = new ArrayList<>();
    protected Set<Transaction> already = Collections.newSetFromMap(new IdentityHashMap<>());
    protected Set<String> beginEncounteredEventKeys = new LinkedHashSet<>();
    protected boolean beginActive;
    protected boolean immediateActive;
    protected boolean closed;

    protected ConcreteTr(Tr tr, String op, UUID id, TransactionLogic tl,
            EventLogic el)
    {
        sane(tr, "tr", op, "op", id, "id", tl, "tl", el, "el");
        OpKey.validate(op);
        this.tr = tr;
        this.op = op;
        this.id = id;
        this.tl = tl;
        this.el = el;
        runtimeIndexes = Creator.create(IndexData.class);
        validationData = Creator.create(ValidationData.class, new Class[] { ConcreteTr.class }, this);
        memos = Creator.create(TrMemos.class);
        for (StageName stageName : StageName.values())
        {
            stageData.put(stageName, Creator.create(StageData.class));
        }
        intentLogic = Creator.create(IntentLogic.class, new Class[] { ConcreteTr.class }, this);
        indexIntents = Creator.create(IndexIntents.class, new Class[] { ConcreteTr.class, IntentLogic.class },
                this, intentLogic);
        executeLifecycle = Creator.create(ExecuteLifecycle.class, new Class[] { ConcreteTr.class, IntentLogic.class },
                this, intentLogic);
    }

    public void addTransaction(Transaction tr)
    {
        sane(tr, "tr");
        verify(already.add(tr), "This transaction is already added");
        tr.op = op;
        transactions.add(tr);
        indexIntents.call(tr);
        if (tr.context != null)
        {
            indexIntents.context(tr.context);
        }
    }

    public void checkpoint() throws ExistentialException
    {
        requireOpen("checkpoint");
        tl.checkpoint(tr);
    }

    public void commit() throws ExistentialException
    {
        requireOpen("commit");
        tl.commit(tr);
    }

    public void rollback() throws ExistentialException
    {
        requireOpen("rollback");
        tl.rollback(tr);
    }

    public <T> void event(Event<T> event, T t, TypeKey<T> type) throws ExistentialException
    {
        requireOpen("send events");
        el.event(event, t, type, tr);
    }

    public <T> void event(BiEvent<T> event, TypeKey<T> type) throws ExistentialException
    {
        requireOpen("send events");
        el.event(event, type, tr);
    }

    public <T> void create(T t, TypeKey<T> type) throws ExistentialException
    {
        requireOpen("send events");
        el.create(t, type, tr);
    }

    public <T> void create(T t) throws ExistentialException
    {
        requireOpen("send events");
        el.create(t, tr);
    }

    public <T> void delete(T t, TypeKey<T> type) throws ExistentialException
    {
        requireOpen("send events");
        el.delete(t, type, tr);
    }

    public <T> void delete(T t) throws ExistentialException
    {
        requireOpen("send events");
        el.delete(t, tr);
    }

    public <T> void update(T t, TypeKey<T> type) throws ExistentialException
    {
        requireOpen("send events");
        el.update(t, type, tr);
    }

    public <T> void update(T t) throws ExistentialException
    {
        requireOpen("send events");
        el.update(t, tr);
    }

    public <T> void transit(T t0, T t1, TypeKey<T> type) throws ExistentialException
    {
        requireOpen("send events");
        el.transit(t0, t1, type, tr);
    }

    public <T> void transit(T t0, T t1) throws ExistentialException
    {
        requireOpen("send events");
        el.transit(t0, t1, tr);
    }

    public <T> void port(T t0, T t1, TypeKey<T> type) throws ExistentialException
    {
        requireOpen("send events");
        el.port(t0, t1, type, tr);
    }

    public <T> void port(T t0, T t1) throws ExistentialException
    {
        requireOpen("send events");
        el.port(t0, t1, tr);
    }

    public <T> void read(T entity, TypeKey<T> type) throws ExistentialException
    {
        requireOpen("send events");
        el.read(entity, type, tr);
    }

    public <T> void read(T entity) throws ExistentialException
    {
        requireOpen("send events");
        el.read(entity, tr);
    }

    public <T> void memo(T before, T live, TypeKey<T> typeKey) throws ExistentialException
    {
        requireOpen("register memo state");
        memos.put(live, before, typeKey);
    }

    public <T> void memo(T before, T live, Class<T> cls) throws ExistentialException
    {
        sane(cls, "cls");
        memo(before, live, TypeKey.valueOf(cls, false));
    }

    public <T> T beforeState(T live, TypeKey<T> typeKey)
    {
        requireMemos();
        return memos.get(live, typeKey);
    }

    public <T> boolean hasMemo(T live, TypeKey<T> typeKey)
    {
        requireMemos();
        return memos.contains(live, typeKey);
    }

    public <T> void write(T entity, TypeKey<T> type) throws ExistentialException
    {
        requireOpen("send events");
        el.write(entity, type, tr);
    }

    public <T> void write(T entity) throws ExistentialException
    {
        requireOpen("send events");
        el.write(entity, tr);
    }

    public void onBegin() throws ExistentialException
    {
        beginActive = true;
        immediateActive = true;
        executeLifecycle.call(Begin.class, StageName.BEGIN);
    }

    public void onCheckpoint() throws ExistentialException
    {
        beginActive = false;
        immediateActive = false;
        executeLifecycle.call(Checkpoint.class, StageName.CHECKPOINT);
    }

    public void onCommit() throws ExistentialException
    {
        beginActive = false;
        immediateActive = false;
        executeLifecycle.call(Commit.class, StageName.COMMIT);
    }

    public void onRollback() throws ExistentialException
    {
        beginActive = false;
        immediateActive = false;
        executeLifecycle.call(Rollback.class, StageName.ROLLBACK);
    }

    public String id()
    {
        return id.toString();
    }

    public List<Transaction> transactions()
    {
        return transactions;
    }

    public IndexData runtimeIndexes()
    {
        return runtimeIndexes;
    }

    public boolean hasIntents()
    {
        return intentLogic.hasIntents();
    }

    public boolean hasIntentEventType(EventType eventType)
    {
        return intentLogic.hasIntentEventType(eventType);
    }

    public boolean hasIntents(StageName stageName)
    {
        return intentLogic.hasIntents(stageName);
    }

    public boolean hasIntentEventType(StageName stageName, EventType eventType)
    {
        return intentLogic.hasIntentEventType(stageName, eventType);
    }

    public boolean hasBiIntentHandler(StageName stageName, EventType eventType, String typeKey)
    {
        return intentLogic.hasBiIntentHandler(stageName, eventType, typeKey);
    }

    public List<EventHandler<?>> intentHandlers(EventType eventType, String typeKey)
    {
        return intentLogic.intentHandlers(eventType, typeKey);
    }

    public List<EventHandler<?>> intentHandlers(StageName stageName, EventType eventType, String typeKey)
    {
        return intentLogic.intentHandlers(stageName, eventType, typeKey);
    }

    public boolean beginActive()
    {
        return beginActive;
    }

    public boolean immediateActive()
    {
        return immediateActive;
    }

    public <T> boolean shouldEvaluateBegin(RuntimeKey<T> runtimeKey)
    {
        sane(runtimeKey, "runtimeKey");
        String key = runtimeKey.key().toString();
        return beginEncounteredEventKeys.add(key);
    }

    public Map<StageName, StageData> stageData()
    {
        return stageData;
    }

    public void close()
    {
        if (closed)
        {
            return;
        }
        closed = true;
        validationData.close();
        validationData = null;
        memos.clear();
        memos = null;
        transactions = null;
        tr = null;
        tl = null;
        el = null;
        already = null;
        stageData = null;
        beginEncounteredEventKeys = null;
        beginActive = false;
        immediateActive = false;
    }

    protected void requireOpen(String action) throws ExistentialException
    {
        if (!closed)
        {
            return;
        }
        String verb = action != null ? action : "perform this operation";
        throw new ExistentialException(String.format(
                "Cannot %s because the transaction is closed. Begin a new transaction for additional work. See %s",
                verb,
                TROUBLESHOOTING_SECTION));
    }

    protected void requireMemos()
    {
        verify(memos != null, "Transaction memo state is not available");
    }
}
