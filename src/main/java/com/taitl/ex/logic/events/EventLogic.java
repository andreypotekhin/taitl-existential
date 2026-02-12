package com.taitl.ex.logic.events;

import java.io.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.events.actions.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

public class EventLogic implements Closeable
{
    protected Existential ex;
    protected ExistentialEvents ev;

    protected ReceiveEvent receiveEvent = Creator.singleton(ReceiveEvent.class);
    protected SplitEvent splitEvent = Creator.singleton(SplitEvent.class);

    public EventLogic(ExistentialEvents ev)
    {
        this.ev = ev;
        this.ex = ev.ex();
    }

    public <T> void event(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(t0, "t0", t1, "t1", type, "type", tranID, "tranID");
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
        sane(t, "t", type, "type", tranID, "tranID");
        // TODO
    }

    /**
     * Variant of send(t0, t1) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Do not use for generic classes, such as Document<T> and the like.
     */
    public <T> void event(T t0, T t1, String tranID) throws ExistentialException
    {
        sane(t0, "t0", t1, "t1", tranID, "tranID");
        // TODO
    }

    /**
     * Variant of send(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Do not use for generic classes, such as Document<T> and the like.
     */
    public <T> void event(T t, String tranID) throws ExistentialException
    {
        sane(t, "t", tranID, "tranID");
        // TODO
    }

    public void close()
    {
    }
}
