package com.taitl.existential.transactions;

import com.taitl.ex.common.helper.*;
import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.logic.evaluation.actions.*;
import com.taitl.ex.logic.indexing.data.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.ex.logic.validation.data.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.transaction_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

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
    public final UUID id;
    public String op;
    protected TransactionLogic tl;
    protected List<Transaction> transactions = new ArrayList<>();
    protected Set<Transaction> already = Collections.newSetFromMap(new IdentityHashMap<>());
    protected IndexData runtimeIndexes;
    protected ValidationData validationData;
    protected Map<StageName, StageData> stageData = new EnumMap<>(StageName.class);
    protected Set<String> preconditionEncounteredEventKeys = new LinkedHashSet<>();
    protected boolean preconditionActive;
    protected boolean immediateActive;

    /**
     * Creates a transaction instance for the given operation and id.
     *
     * @param op
     *            Operation name
     * @param id
     *            Transaction identifier
     */
    public Tr(String op, UUID id, TransactionLogic tl)
    {
        sane(op, "op", id, "id");
        OpKey.validate(op);
        this.op = op;
        this.id = id;
        this.tl = tl;
        runtimeIndexes = new IndexData();
        validationData = new ValidationData(this);
        for (StageName stageName : StageName.values())
        {
            stageData.put(stageName, new StageData());
        }
    }

    /**
     * Registers a transaction configuration instance for this operation.
     *
     * @param tr
     *            Transaction configuration to add
     */
    public void addTransaction(Transaction tr)
    {
        sane(tr, "tr");
        State.verify(already.add(tr), "This transaction is already added");
        tr.op = op;
        transactions.add(tr);
        indexIntents(tr);
        if (tr.context != null)
        {
            indexContextIntents(tr.context);
        }
    }

    /**
     * Creates a checkpoint in the transaction lifecycle.
     * Performs validation of the rules configured for the transaction's business operation.
     * After commit, transaction object is still usable: more events can be sent.
     *
     * @throws ExistentialException when checkpoint fails
     */
    public void checkpoint() throws ExistentialException
    {
        tl.checkpoint(this);
    }

    /**
     * Commits a transaction.
     * Performs validation of the rules configured for the transaction's business operation.
     * After commit, transaction object becomes unusable, tranID becomes invalid.
     *
     * @throws ExistentialException when validation or commit fails
     */
    public void commit() throws ExistentialException
    {
        tl.commit(this);
    }

    /**
     * Rolls back transaction.
     * Rule validation is not performed.
     * After commit, transaction object becomes unusable, tranID becomes invalid.
     *
     * @throws ExistentialException when rollback fails
     */
    public void rollback() throws ExistentialException
    {
        tl.rollback(this);
    }

    /* Lifecycle events */

    /**
     * Called when a transaction begins.
     * Invoked by BeginTr.
     */
    public void onBegin() throws ExistentialException
    {
        preconditionActive = true;
        immediateActive = true;
        executeLifecycle(Begin.class);
    }

    /**
     * Called when a transaction checkpoint is reached.
     * Invoked by CheckpointTr.
     */
    public void onCheckpoint() throws ExistentialException
    {
        preconditionActive = false;
        immediateActive = false;
        executeLifecycle(Checkpoint.class);
    }

    /**
     * Called when a transaction is committed.
     * Invoked by CommitTr.
     */
    public void onCommit() throws ExistentialException
    {
        preconditionActive = false;
        immediateActive = false;
        executeLifecycle(Commit.class);
    }

    /**
     * Called when a transaction is rolled back.
     * Invoked by RollbackTr.
     */
    public void onRollback() throws ExistentialException
    {
        preconditionActive = false;
        immediateActive = false;
        executeLifecycle(Rollback.class);
    }

    /* Attributes */

    /**
     * Returns the transaction identifier as a UUID string.
     * @return Transaction id string
     */
    public String id()
    {
        return id.toString();
    }

    /**
     * Returns the list of Transaction objects associated with this transaction,
     * in the order of their Context declaration.
     *
     * @return List of Transaction configurations
     */
    public List<Transaction> transactions()
    {
        return transactions;
    }

    /**
     * Returns runtime indexes used for fast evaluation of expressions.
     *
     * @return Runtime index data
     */
    public IndexData runtimeIndexes()
    {
        return runtimeIndexes;
    }

    public boolean hasIntentEventTypes()
    {
        for (StageName stageName : StageName.values())
        {
            if (hasIntentEventTypes(stageName))
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasIntentEventType(EventType eventType)
    {
        for (StageName stageName : StageName.values())
        {
            if (hasIntentEventType(stageName, eventType))
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasIntentEventTypes(StageName stageName)
    {
        sane(stageName, "stageName");
        StageData data = stageData.get(stageName);
        return data != null && !data.intentEventTypes.isEmpty();
    }

    public boolean hasIntentEventType(StageName stageName, EventType eventType)
    {
        sane(stageName, "stageName");
        sane(eventType, "eventType");
        StageData data = stageData.get(stageName);
        return data != null && data.intentEventTypes.contains(eventType);
    }

    public List<EventHandler<?>> intentHandlers(EventType eventType, String typeKey)
    {
        return intentHandlers(StageName.IMMEDIATE, eventType, typeKey);
    }

    public List<EventHandler<?>> intentHandlers(StageName stageName, EventType eventType, String typeKey)
    {
        sane(stageName, "stageName");
        sane(eventType, "eventType", typeKey, "typeKey");
        StageData data = stageData.get(stageName);
        return data != null ? data.intentHandlers.get(intentKey(eventType, typeKey)) : null;
    }

    protected void indexIntents(Transaction tr)
    {
        sane(tr, "tr");
        for (StageName stageName : StageName.values())
        {
            for (Evs<?> evs : tr.stage().at(stageName))
            {
                if (!(evs instanceof Intent<?>))
                {
                    continue;
                }
                indexIntent(stageName, (Intent<?>) evs);
            }
        }
    }

    protected void indexIntent(StageName stageName, Intent<?> intent)
    {
        sane(stageName, "stageName", intent, "intent");
        StageData data = stageData.get(stageName);
        sane(data, "data");
        String typeKey = intent.typeKey().toString();
        for (Ev<?> ev : intent.list())
        {
            if (!(ev instanceof EventHandler<?>))
            {
                continue;
            }
            EventHandler<?> handler = (EventHandler<?>) ev;
            EventType eventType = handler.eventType();
            data.intentEventTypes.add(eventType);
            data.intentHandlers.put(intentKey(eventType, typeKey), handler);
        }
    }

    protected static IntentHandlerKey intentKey(EventType eventType, String typeKey)
    {
        sane(eventType, "eventType", typeKey, "typeKey");
        return new IntentHandlerKey(eventType, typeKey);
    }

    protected void indexContextIntents(Context context)
    {
        sane(context, "context");
        for (StageName stageName : StageName.values())
        {
            for (Evs<?> evs : context.stage().at(stageName))
            {
                if (!(evs instanceof Intent<?>))
                {
                    continue;
                }
                indexIntent(stageName, (Intent<?>) evs);
            }
        }
    }

    public boolean preconditionActive()
    {
        return preconditionActive;
    }

    public boolean immediateActive()
    {
        return immediateActive;
    }

    public <T> boolean shouldEvaluatePrecondition(RuntimeKey<T> runtimeKey)
    {
        sane(runtimeKey, "runtimeKey");
        String key = runtimeKey.key().toString();
        return preconditionEncounteredEventKeys.add(key);
    }

    protected void executeLifecycle(Class<?> eventClass) throws ExistentialException
    {
        sane(eventClass, "eventClass");
        for (Transaction transaction : transactions)
        {
            executeLifecycle(eventClass, transaction);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void executeLifecycle(Class<?> eventClass, Transaction transaction) throws ExistentialException
    {
        sane(eventClass, "eventClass", transaction, "transaction");
        for (StageName stageName : StageName.values())
        {
            for (Evs<?> evs : transaction.stage().at(stageName))
            {
                if (!(evs instanceof Life<?>))
                {
                    continue;
                }
                Life<?> life = (Life<?>) evs;
                if (!matchesType(life.typeKey(), transaction))
                {
                    continue;
                }
                for (Ev<?> ev : life.list())
                {
                    if (!(ev instanceof EventHandler<?>))
                    {
                        continue;
                    }
                    EventHandler<?> eventHandler = (EventHandler<?>) ev;
                    if (!eventClass.equals(eventHandler.eventType().eventClass()))
                    {
                        continue;
                    }
                    if (!(eventHandler instanceof On<?>))
                    {
                        throw new IllegalStateException("Lifecycle handler must extend On, got "
                                + eventHandler.getClass().getName());
                    }
                    ExecuteHandler.handle((On) eventHandler, transaction);
                }
            }
        }
    }

    protected boolean matchesType(TypeKey<?> configuredType, Transaction transaction)
    {
        sane(configuredType, "configuredType", transaction, "transaction");
        Class<?> type = transaction.getClass();
        while (type != null && Transaction.class.isAssignableFrom(type))
        {
            TypeKey<?> shortName = TypeKey.valueOf(type, false);
            TypeKey<?> fullName = TypeKey.valueOf(type, true);
            if (configuredType.equals(shortName) || configuredType.equals(fullName))
            {
                return true;
            }
            type = type.getSuperclass();
        }
        return false;
    }

    protected static class IntentHandlerKey
    {
        protected final EventType eventType;
        protected final String typeKey;

        protected IntentHandlerKey(EventType eventType, String typeKey)
        {
            sane(eventType, "eventType", typeKey, "typeKey");
            this.eventType = eventType;
            this.typeKey = typeKey;
        }

        public int hashCode()
        {
            return Objects.hash(eventType, typeKey);
        }

        public boolean equals(Object other)
        {
            if (other == this)
            {
                return true;
            }
            if (!(other instanceof IntentHandlerKey))
            {
                return false;
            }
            IntentHandlerKey key = (IntentHandlerKey) other;
            return eventType.equals(key.eventType) && typeKey.equals(key.typeKey);
        }
    }

    protected static class StageData
    {
        protected final Set<EventType> intentEventTypes = new LinkedHashSet<>();
        protected final ListMap<IntentHandlerKey, EventHandler<?>> intentHandlers = new ListMap<>();
    }

    /**
     * Releases transaction resources and clears internal collections.
     */
    public void close()
    {
        validationData.close();
        validationData = null;
        transactions = null;
        tl = null;
        already = null;
        stageData = null;
        preconditionEncounteredEventKeys = null;
        preconditionActive = false;
        immediateActive = false;
    }
}
