package com.taitl.ex.core.existential;

import java.io.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

public class ExistentialEvents implements Closeable
{
    protected Existential ex;

    public ExistentialEvents(Existential ex)
    {
        this.ex = ex;
    }

    public <T> void event(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        check(t0 != null || t1 != null, "One of t0, t1 must not be null");
        // Get transaction object
        // Address the case when tran is not found
        // Split event into multiple events using EventSplitter
        // Transition<House> -> On<House>, Mutate<House>, Transit<House>
        // Depending on mutation type: OnCreate<House>, OnUpdate<House>, OnMutate<House>,
        // OnDelete<House>
        // Trigger processing of immediate event handlers
        // Add event to event field for late-phase processing
    }

    public <T> void event(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        // TODO
    }

    /**
     * Variant of send(t0, t1) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Do not use for generic classes, such as Document<T> and the like.
     */
    public <T> void event(T t0, T t1, String tranID) throws ExistentialException
    {
        // TODO
    }

    /**
     * Variant of send(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Do not use for generic classes, such as Document<T> and the like.
     */
    public <T> void event(T t, String tranID) throws ExistentialException
    {
        // TODO
    }

    public void close()
    {
    }
}
