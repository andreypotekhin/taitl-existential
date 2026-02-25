package com.taitl.existential.transactions;

import com.taitl.ex.common.helper.State;
import com.taitl.ex.logic.indexing.data.IndexData;
import com.taitl.ex.logic.transactions.TransactionLogic;
import com.taitl.ex.logic.validation.data.ValidationData;
import com.taitl.existential.configs.Transaction;
import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.keys.OpKey;

import java.util.*;

import static com.taitl.ex.common.helper.Args.sane;

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
    }
}
