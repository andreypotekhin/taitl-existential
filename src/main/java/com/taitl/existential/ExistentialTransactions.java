package com.taitl.existential;

public class ExistentialTransactions
{
    public String begin(String op)
    {
        // Create transaction object
        // Assign unique ID
        // Add to TranactionRegistry
        return "DUMMY";
    }

    public void commit(String tranID)
    {
        // TODO
        // Find transaction object in TranactionRegistry
        // Care for scenarios when tran not found
        // Commit transaction - evaluate validation expressions
        // Close transaction, remove from registry
    }

    public void rollback(String tranID)
    {
        // TODO
        // Find transaction object in TranactionRegistry
        // Care for scenarios when tran not found
        // Close transaction, remove from registry
    }
}
