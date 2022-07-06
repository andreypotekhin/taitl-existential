package com.taitl.existential;

import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.helper.Args;
import com.taitl.existential.keys.TypeKey;

public class ExistentialEvents
{
    protected Existential ex;

    public ExistentialEvents(Existential ex)
    {
        this.ex = ex;
    }

    public <T> void send(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        Args.require(t0 != null || t1 != null, "One of t0, t1 must not be null");
        // Get transaction object
        // Care for scenario when tran is not found
        // Split event into multiple using EventSplitter
        // Transition<House> -> On<House>, Mutate<House>, Transit<House>
        // Depending on permutation type: OnCreate<House>, OnUpdate<House>, OnMutate<House>,
        // OnDelete<House>
        // Trigger event processing for events with side effects
        // Store (postpone) event processing for events without side effects
    }

    public <T> void send(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
    }

    /**
     * Variant of send(t0, t1) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Do not use for generic classes, such as Document<T> and the like.
     */
    public <T> void send(T t0, T t1, String tranID) throws ExistentialException
    {
    }

    /**
     * Variant of send(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Do not use for generic classes, such as Document<T> and the like.
     */
    public <T> void send(T t, String tranID) throws ExistentialException
    {
    }

    /*
     * public <T, E extends Event<T>> void send(E event, String tranID) throws ExistentialException
     * { // Get transaction object // Care for scenario when tran is not found // Split event into
     * multiple using EventSplitter // Transition<House> -> On<House>, Mutate<House>, Transit<House>
     * // Depending on permutation type: OnCreate<House>, OnUpdate<House>, OnMutate<House>, //
     * OnDelete<House> // Trigger event processing for events with side effects // Store (postpone)
     * event processing for events without side effects }
     *
     * public <E> void entity(E event, String tranID) throws ExistentialException { }
     */
}
