package com.taitl.existential.instructions;

import com.taitl.existential.expressions.Expression;
import com.taitl.existential.handler.types.EventHandler;
import com.taitl.existential.helper.Args;

public class Instruction<T>
{
	protected enum InstructionType
	{
		EXPRESSION, HANDLER, REF, SPECIAL
	}

	protected EventHandler<T> handler;
	protected Expression<T> expression;
	protected InstructionType type;

	public Instruction(EventHandler<T> handler)
	{
		Args.cool(handler, "handler");
		this.handler = handler;
		this.type = InstructionType.HANDLER;
	}

	public Instruction(Expression<T> expression)
	{
		Args.cool(expression, "expression");
		this.expression = expression;
		this.type = InstructionType.EXPRESSION;
	}

	/**
	 * For subclasses.
	 * @see TransactionRef
	 */
	protected Instruction()
	{
		this.type = InstructionType.SPECIAL;
	}

}
