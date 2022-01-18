package com.taitl.existential;

/**
 * Main entry point into Existential library. See documentation on how to use.
 * <p>
 * Documentation: 
 *   https://github.com/andreypotekhin/taitl-existential
 * <p>
 * @author Andrey Potekhin
 * @see ExistentialTransactions
 * @see ExistentialEvents
 */
public class ExistentialLibrary
{
    public static ExistentialTransactions transactions = new ExistentialTransactions();
    public static ExistentialEvents events = new ExistentialEvents();
}
