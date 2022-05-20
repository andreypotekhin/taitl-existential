package com.taitl.existential.transactions;

import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.taitl.existential.keys.EventKey;
import com.taitl.existential.constants.Strings;

/**
 * Stores all event types for which some rule is defined in a Transaction or Context.
 *
 * Different contexts may react to different types of events.
 * To speed up the answer to question 'which events should be emitted by EventSplitter for this context', the set of
 * relevant events (from the context as well from all its parents) is created at transaction start and stored in the
 * Transaction object. This allows to avoid having to gather such info for each event from each involved context.
 *
 * @author Andrey Potekhin
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

    TransactionEvents(Transaction tr)
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
