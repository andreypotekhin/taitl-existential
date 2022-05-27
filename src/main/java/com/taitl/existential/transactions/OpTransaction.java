package com.taitl.existential.transactions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;
import com.taitl.existential.keys.OpKey;

/**
 * Defines an Operation Transaction - a set of Transaction objects relevant
 * for a single business operation.
 *
 * There may be multiple Contexts which apply to a business operation (parent-child
 * contexts as well as wildcard contexts). Each context may create one or several
 * Transaction objects, depending on the number of custom Transaction factories
 * specified in it.
 *
 * This class's job is to hold all Transaction objects created by these various
 * contexts, as well as multiple ones created by any of them.
 *
 * The Transactions may declare entity event handlers, which result in side effects,
 * so the order of transactions is important.
 *
 * The order of transactions follows the order of declaration of their corresponding
 * contexts (contexts which are applicable to business operation). Within each context,
 * the order of transactions is same as the order of declaration of its transaction
 * factories.
 */
public class OpTransaction
{
    public UUID id;
    String op;
    List<Transaction> transactions = new ArrayList<>();

    public OpTransaction(String op, UUID id)
    {
        Args.cool(op, "op", id, "id");
        OpKey.requireValidName(op);
        this.op = op;
        this.id = id;
    }

    public void addTransaction(Transaction tr)
    {
        Args.cool(tr, "tr");
        State.verify(!transactions.contains(tr), "This transactions is already added");
        transactions.add(tr);
    }

    // TODO begin()
    // commit()
    // Commit transactions - run handlers and evaluate validation expressions
    // Close transactions, remove op transaction from registry

    // rollback()

}
