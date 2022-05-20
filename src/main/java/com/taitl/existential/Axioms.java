package com.taitl.existential;

import com.taitl.existential.transactions.Transaction;

public class Axioms<T>
{
	private Transaction tran;

	public void setTransaction(Transaction tr)
	{
		tran = tr;
	}

}
