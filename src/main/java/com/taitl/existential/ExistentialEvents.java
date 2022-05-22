package com.taitl.existential;

import com.taitl.existential.event.types.Event;
import com.taitl.existential.exceptions.ExistentialException;

public class ExistentialEvents
{
    public <T, E extends Event<T>> void send(E event, String tranID) throws ExistentialException
    {
        // Get transaction object
        // Care for scenario when tran is not found
        // Split event into multiple using EventSplitter
        // Transition<House> -> On<House>, Mutate<House>, Transit<House>
        // Depending on permutation type: OnCreate<House>, OnUpdate<House>, OnMutate<House>,
        // OnDelete<House>
        // Trigger event processing for events with side effects
        // Store (postpone) event processing for events without side effects
    }

    public <T> void transit(T t0, T t1, String tranID) throws ExistentialException
    {
    }

    public <E> void entity(E event, String tranID) throws ExistentialException
    {
    }

    public <T> void read(T entity, String tranID) throws ExistentialException
    {
    }

}
