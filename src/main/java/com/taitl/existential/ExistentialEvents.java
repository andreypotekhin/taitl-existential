package com.taitl.existential;

import com.taitl.existential.event.base.Event;

public class ExistentialEvents
{
    public <T> void mutate(T t0, T t1, String tranID)
    {
    }

    public <E> void entity(E entity, String tranID)
    {
    }

    public <T> void read(T entity, String tranID)
    {
    }

    public <T, E extends Event<T>> void send(E event, String tranID)
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
