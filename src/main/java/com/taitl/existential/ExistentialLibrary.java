package com.taitl.existential;

/**
 * Main entry point into Existential library. See documentation on how to use.
 * <p>
 * Documentation: 
 *   https://github.com/andreypotekhin/taitl-existential
 *  
 * @author Andrey Potekhin
 * @see ExistentialTransactions
 * @see ExistentialEvents
 */
public class ExistentialLibrary
{
    public static ExistentialLibrary ex = new ExistentialLibrary();
    public ExistentialTransactions transactions = new ExistentialTransactions();
    public ExistentialEvents events = new ExistentialEvents();
}
