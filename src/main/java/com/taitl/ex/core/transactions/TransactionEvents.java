package com.taitl.ex.core.transactions;

import java.util.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

/**
 * Stores all event types for which some rule is defined in a Transaction or Context.
 *
 * Different contexts may react to different types of events.
 * To speed up the answer to question 'which events should be emitted by EventSplitter for this context', the set of
 * relevant events (from the context as well from all its parents) is created at transaction start and stored in the
 * Transaction object. This allows to avoid having to gather such info for each event from each involved context.
 *
 * @see Transaction
 *
 * TODO: Move to Context?
 */
public class TransactionEvents
{
    Transaction tr;
    Set<String> eventTypes = new LinkedHashSet<>();
    BitSet eventTypeMask = new BitSet(64);

    public TransactionEvents(Transaction tr)
    {
        this.tr = tr;
    }

    void addEventType(EventKey eventKey)
    {
        if (eventKey == null)
        {
            throw new IllegalArgumentException(Strings.ARG_EVENT_KEY);
        }
        if (!eventTypes.contains(eventKey.toString()))
        {
            eventTypes.add(eventKey.toString());
            // TODO
            // int eventBit = context.еventSplitter.getEventBit(eventType);
            // eventTypeMask.set(eventBit)
        }
    }

    // TODO: get set of relevant event types
}
