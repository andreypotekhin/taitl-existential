package com.taitl.existential.instructions;

import com.taitl.existential.handler.types.EventHandler;
import com.taitl.existential.helper.Args;

public class Instruction<T>
{
	protected EventHandler<T> handler;

	public Instruction(EventHandler<T> handler)
	{
		Args.cool(handler, "handler");
		this.handler = handler;
	}

	public EventHandler<T> getHandler()
	{
		return handler;
	}
}
