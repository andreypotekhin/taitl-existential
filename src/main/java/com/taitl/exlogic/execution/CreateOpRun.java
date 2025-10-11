package com.taitl.exlogic.execution;

import com.taitl.existential.exceptions.*;
import com.taitl.existential.helper.*;
import com.taitl.existential.keys.*;
import com.taitl.exlogic.transaction.*;

public class CreateOpRun
{
    Executions executions;

    public CreateOpRun(Executions executions)
    {
        Args.cool(executions, "execution");
        this.executions = executions;
    }

    public OpRun call(String op) throws ExistentialException
    {
        Args.cool(op, "op");
        OpKey.validate(op);
        executions.ex().ops().finalise();
        return executions.registry().create(op);
    }
}
