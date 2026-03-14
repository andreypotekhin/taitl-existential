package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.logic.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Shared base for transaction actions.
 */
public abstract class TranAction
{
    @Up
    protected final TransactionLogic tl;

    protected TranAction(TransactionLogic tl)
    {
        sane(tl, "tl");
        this.tl = tl;
    }
}
