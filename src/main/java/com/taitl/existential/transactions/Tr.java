package com.taitl.existential.transactions;

import java.util.*;
import com.taitl.ex.common.helper.*;
import com.taitl.ex.logic.indexing.data.runtime_indexes.*;
import com.taitl.ex.logic.validation.data.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Defines an existential transaction - backbone of this library transaction model.
 * Holds a set of Transaction objects (rule configurations) that apply to a single business operation.
 * Since there may be multiple Contexts applicable to a business operation
 * (parent-child contexts as well as matching wildcard contexts),
 * this class's job is to hold Transaction objects created by each of these Contexts,
 * as means of accessing their rules.
 * The order of Transactions follows the order of declaration of their corresponding
 * Contexts (contexts which are applicable to business operation). Parent contexts are
 * thought to be declared before child contexts. Wildcard contexts are before specific contexts.
 */
public class Tr
{
    public final UUID id;
    public String op;
    List<Transaction> transactions = new ArrayList<>();
    RuntimeIndexes runtimeIndexes;
    ValidationData validationData;

    public Tr(String op, UUID id)
    {
        sane(op, "op", id, "id");
        OpKey.validate(op);
        this.op = op;
        this.id = id;
        runtimeIndexes = new RuntimeIndexes();
        validationData = new ValidationData(this);
    }

    public void addTransaction(Transaction tr)
    {
        sane(tr, "tr");
        State.verify(!transactions.contains(tr), "This transaction is already added");
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
     * On begin transaction.
     * Called by BeginTr
     */
    public void onBegin()
    {
        // Init ValidationData
    }

    /**
     * On transaction checkpoint.
     * Called by CheckpointTr
     */
    public void onCheckpoint()
    {
    }

    /**
     * On commit transaction.
     * Called by CommitTr
     */
    public void onCommit()
    {
    }

    /**
     * On rollback transaction.
     * Called by RollbackTr
     */
    public void onRollback()
    {
    }

    public void close()
    {
        validationData.close();
        validationData = null;
        transactions = null;
    }

    public RuntimeIndexes runtimeIndexes()
    {
        return runtimeIndexes;
    }
}
