package com.taitl.exlogic.existential;

import com.taitl.existential.*;
import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.helper.Args;
import com.taitl.existential.keys.TypeKey;

import java.io.Closeable;

public class ExistentialEvents implements Closeable
{
    protected Existential ex;

    public ExistentialEvents(Existential ex)
    {
        this.ex = ex;
    }

    public <T> void event(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        Args.require(t0 != null || t1 != null, "One of t0, t1 must not be null");
        // Get transaction object
        // Care for scenario when tran is not found
        // Split event into multiple using EventSplitter
        // Transition<House> -> On<House>, Mutate<House>, Transit<House>
        // Depending on mutation type: OnCreate<House>, OnUpdate<House>, OnMutate<House>,
        // OnDelete<House>
        // Trigger event processing for events with side effects
        // Store (postpone) event processing for events without side effects
    }

    public <T> void event(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
    }

    /**
     * Variant of send(t0, t1) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Do not use for generic classes, such as Document<T> and the like.
     */
    public <T> void event(T t0, T t1, String tranID) throws ExistentialException
    {
    }

    /**
     * Variant of send(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Do not use for generic classes, such as Document<T> and the like.
     */
    public <T> void event(T t, String tranID) throws ExistentialException
    {
    }

    public void close()
    {
    }
}
