package com.taitl.ex.core.transactions;

import java.util.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

/**
 * Stores all event types for which a rule is defined in a Transaction or a Context.
 *
 * Different contexts may have rules to different types of events.
 * To speed up the answer to question 'which events should be emitted by EventSplitter for this context?',
 * the set of relevant events (from the context as well from all its parents) is created at transaction
 * start and stored in the Transaction object. This allows to avoid having to gather such info for each
 * individual event from each involved context.
 *
 * @see Transaction
 *
 * TODO: Move to Tr
 */
public class TransactionEventKeys
{
    Transaction tr;
    Set<String> eventKeys = new LinkedHashSet<>();
    BitSet eventTypeMask = new BitSet(64);

    public TransactionEventKeys(Transaction tr)
    {
        this.tr = tr;
    }

    void addEventType(EventKey eventKey)
    {
        if (eventKey == null)
        {
            throw new IllegalArgumentException(Strings.ARG_EVENT_KEY);
        }
        if (!eventKeys.contains(eventKey.toString()))
        {
            eventKeys.add(eventKey.toString());
            // TODO
            // int eventBit = context.еventSplitter.getEventBit(eventType);
            // eventTypeMask.set(eventBit)
        }
    }

    // TODO: get set of relevant event types
}
