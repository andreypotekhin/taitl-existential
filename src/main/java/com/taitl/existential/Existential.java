package com.taitl.existential;

import java.io.Closeable;

/**
 * Main entry point into Existential library. See documentation on how to use.
 * <p>
 *
 * Documentation:
 *   <a href="https://github.com/andreypotekhin/taitl-existential">Existential</a>
 *
 * @author Andrey Potekhin
 *
 * @see ExistentialTransactions
 * @see ExistentialEvents
 * @see ExistentialFlags
 * @see ExistentialContexts
 * @see ExistentialAccess
 */
public final class Existential implements Closeable
{
    public ExistentialTransactions transactions = new ExistentialTransactions(this);
    public ExistentialEvents events = new ExistentialEvents(this);
    public ExistentialFlags flags = new ExistentialFlags(this);
    public ExistentialContexts contexts = new ExistentialContexts(this);
    public ExistentialAccess access = new ExistentialAccess(this);
    public boolean finalized = false;

    public void close()
    {
    }
}
