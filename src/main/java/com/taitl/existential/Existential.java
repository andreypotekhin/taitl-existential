package com.taitl.existential;

/**
 * Main entry point into Existential library. See documentation on how to use.
 * <p>
 * Documentation:
 *   https://github.com/andreypotekhin/taitl-existential
 *
 * @author Andrey Potekhin
 *
 * @see ExistentialTransactions
 * @see ExistentialEvents
 */
public class Existential
{
	public static Existential ex = new Existential();
	private ExistentialTransactions transactions = new ExistentialTransactions();
	private ExistentialEvents events = new ExistentialEvents();
	// TODO: ExistentialBehavior flags
	// TODO: ExistentialContexts contexts
}
