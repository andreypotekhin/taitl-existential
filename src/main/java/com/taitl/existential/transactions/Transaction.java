package com.taitl.existential.transactions;

import javax.naming.Context;

import com.taitl.existential.constants.Strings;

/**
 * Implements a transaction object - a unit of processing within a context.
 * 
 * 'Transactions' in this library are not related to any database transactions, although when integrating, they assumed
 * to be aligned with ones (if you have such). They are simply markers of beginning and end of processing - a business
 * transaction, web request etc.
 * 
 * Transaction's end (commit) serves as the point where we evaluate expressions, such as All and Exists, existing in the
 * current Context as well as its parent Contexts.
 * 
 * @author Andrey Potekhin
 * @see Context
 */
public class Transaction
{
    String id;

    public Transaction(String id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException(Strings.ARG_ID);
        }
        this.id = id;
    }

    // TODO
}
