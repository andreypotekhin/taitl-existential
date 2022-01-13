package com.taitl.existential;

public class ExistentialEvents
{
    public <T> void send(T event, String tranID)
    {
        // Get transaction object
        // Care for scenario when tran not found
        // Split event into multiple
        // Permutation<House> -> On<House>, Mutation<House>, Permutation<House>
        // Depending on permutation type: OnUpdate<House>, OnMutate<House>, OnDelete<House>
        // Trigger event processing chain for events with side effects
        // Store (postpone) event processing chain for events without side effects
    }
}
