package com.taitl.ex.core.instructions;

import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Implements a reference to a set of instructions elsewhere.
 *
 * Used to refer from instructions in a Context to instructions
 * defined in its custom Transactions.
 *
 * @param <T> Entity type
 */
public class TransactionRef<T> extends Instruction<T>
{
    protected Transaction tran;

    public TransactionRef(Transaction tran)
    {
        super();
        sane(tran, "tran");
        this.tran = tran;
        this.type = InstructionType.REF;
    }
}
