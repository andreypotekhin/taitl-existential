package com.taitl.existential;

/**
 * Main entry point into Existential library. See documentation on how to use.
 * <p>
 *
 * Documentation:
 *   https://github.com/andreypotekhin/taitl-existential
 *
 * @author Andrey Potekhin
 *
 * @see ExistentialTransactions
 * @see ExistentialEvents
 * @see ExistentialFlags
 * @see ExistentialContexts
 * @see ExistentialAccess
 */
public final class Existential
{
    public static Existential ex = new Existential();
    public static ExistentialTransactions transactions = new ExistentialTransactions();
    public static ExistentialEvents events = new ExistentialEvents();
    public static ExistentialFlags flags = new ExistentialFlags();
    public static ExistentialContexts contexts = new ExistentialContexts();
    public static ExistentialAccess access = new ExistentialAccess();
    protected boolean finalized = false;
}
