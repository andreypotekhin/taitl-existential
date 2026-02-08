package com.taitl.ex.core.execution;

import java.util.*;
import com.taitl.ex.common.helper.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

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
public class OpRun
{
    public UUID id;
    String op;
    List<Transaction> transactions = new ArrayList<>();
    // TODO: Add collections for events, immediates, validations.

    public OpRun(String op, UUID id)
    {
        sane(op, "op", id, "id");
        OpKey.validate(op);
        this.op = op;
        this.id = id;
    }

    public void addTransaction(Transaction tr)
    {
        sane(tr, "tr");
        State.verify(!transactions.contains(tr), "This transaction is already added");
        tr.op = op;
        transactions.add(tr);
    }

    // TODO begin()
    // commit()
    // Commit transactions - run handlers and evaluate validation expressions
    // Close transactions, remove op transaction from registry

    // rollback()

}
