package com.taitl.ex.logic.tr.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.concrete.*;
import com.taitl.ex.logic.tr.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;

import static com.taitl.ex.common.helper.Args.*;

public class IndexIntents
{
    @Up
    protected final ConcreteTr tr;

    @Logic
    protected final IntentLogic intentLogic;

    public IndexIntents(ConcreteTr tr, IntentLogic intentLogic)
    {
        sane(tr, "tr", intentLogic, "intentLogic");
        this.tr = tr;
        this.intentLogic = intentLogic;
    }

    public void call(Transaction transaction)
    {
        sane(transaction, "transaction");
        for (StageName stageName : StageName.values())
        {
            for (Evs<?> evs : transaction.stage().at(stageName))
            {
                if (!(evs instanceof Intent<?>))
                {
                    continue;
                }
                intentLogic.indexIntent(stageName, (Intent<?>) evs);
            }
        }
    }

    public void context(Context context)
    {
        intentLogic.indexContextIntents(context);
    }
}
