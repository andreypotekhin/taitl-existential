package com.taitl.ex.logic.events.actions;

import com.taitl.ex.logic.events.logic.*;

/**
 * Maps an incoming macro-event to a set of basic, fine-grained events that
 * constraints may be attached to.
 * Example:
 * Incoming event: ex.event(null, cat, tran);
 * Resulting events: Create(cat)
 * Incoming event: ex.event(cat, null, tran);
 * Resulting events: Delete(cat)
 * TODO: use EvenSplitter
 * @see EventSplitter
 */
public class SplitEvent
{
}
