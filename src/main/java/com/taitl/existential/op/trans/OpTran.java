package com.taitl.existential.op.trans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.taitl.existential.helper.Args;
import com.taitl.existential.transactions.Transaction;

/**
 * Defines an Operation Transaction.
 *
 * There may be multiple Contexts which apply to a business operation (wildcard
 * contexts). Each context may create one or several Transaction objects,
 * depending on the number of custom Transaction factories specified in it.
 *
 * This class holds all Transaction objects created by these various contexts,
 * as well as multiple ones created by any of them.
 *
 * Transactions may declare entity event handlers, which result in side effects,
 * so the order of transactions is important.
 *
 * The order of transactions follows the order of declaration of their corresponding
 * contexts (contexts which are applicable to business operation). Within each context,
 * the order of transactions is same as the order of declaration of its transaction
 * factories.
 */
public class OpTran
{
    public UUID id;
    String op;
    List<Transaction> transactions = new ArrayList<>();

    public OpTran(String op, UUID id)
    {
        Args.cool(op, "op", id, "id");
        this.op = op;
        this.id = id;
    }
}
