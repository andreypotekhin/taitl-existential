package com.taitl.ex.common.helper;

import com.taitl.ex.logic.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Shared base for transaction action classes.
 */
public abstract class TransactionActionSupport
{
    protected final TransactionLogic tl;

    protected TransactionActionSupport(TransactionLogic tl)
    {
        sane(tl, "transactionLogic");
        this.tl = tl;
    }
}
