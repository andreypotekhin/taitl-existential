package com.taitl.existential.transactions;

import java.util.*;
import com.taitl.ex.common.helper.*;
import com.taitl.ex.logic.indexing.data.*;
import com.taitl.ex.logic.validation.data.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.keys.*;

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
    List<Transaction> transactions = new ArrayList<>();
    Set<Transaction> already = Collections.newSetFromMap(new IdentityHashMap<>());
    IndexData runtimeIndexes;
    ValidationData validationData;

    /**
     * Creates a transaction instance for the given operation and id.
     *
     * @param op
     *            Operation name
     * @param id
     *            Transaction identifier
     */
    public Tr(String op, UUID id)
    {
        sane(op, "op", id, "id");
        OpKey.validate(op);
        this.op = op;
        this.id = id;
        runtimeIndexes = new IndexData();
        validationData = new ValidationData(this);
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
    }

    // TODO
    // begin()
    // commit()
    // Commit transactions - run handlers and evaluate validation expressions
    // Close transactions, remove op transaction from registry
    // rollback()
    // checkpoint()

    /**
     * Called when a transaction begins.
     * Invoked by BeginTr.
     */
    public void onBegin()
    {
        // Init ValidationData
    }

    /**
     * Called when a transaction checkpoint is reached.
     * Invoked by CheckpointTr.
     */
    public void onCheckpoint()
    {
    }

    /**
     * Called when a transaction is committed.
     * Invoked by CommitTr.
     */
    public void onCommit()
    {
    }

    /**
     * Called when a transaction is rolled back.
     * Invoked by RollbackTr.
     */
    public void onRollback()
    {
    }

    /**
     * Releases transaction resources and clears internal collections.
     */
    public void close()
    {
        validationData.close();
        validationData = null;
        transactions = null;
        already = null;
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
}
