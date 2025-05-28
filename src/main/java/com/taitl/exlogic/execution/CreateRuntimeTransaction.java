package com.taitl.exlogic.execution;

import com.taitl.existential.exceptions.*;
import com.taitl.existential.helper.*;
import com.taitl.existential.keys.*;
import com.taitl.exlogic.transaction.*;

public class CreateRuntimeTransaction
{
    Executions executions;

    public CreateRuntimeTransaction(Executions executions)
    {
        Args.cool(executions, "execution");
        this.executions = executions;
    }

    public RuntimeTransaction call(String op) throws ExistentialException
    {
        Args.cool(op, "op");
        OpKey.validate(op);
        executions.ex().contexts().finalizeSetup();
        return executions.registry().create(op);
    }
}
