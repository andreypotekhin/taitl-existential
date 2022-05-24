package com.taitl.existential.instructions;

import com.taitl.existential.helper.Args;
import com.taitl.existential.transactions.Transaction;

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
		Args.cool(tran, "tran");
		this.tran = tran;
		this.type = InstructionType.REF;
	}
}
