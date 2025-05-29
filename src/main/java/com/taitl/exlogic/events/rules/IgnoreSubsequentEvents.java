package com.taitl.exlogic.events.rules;

/**
 * Ignore an event if it reported more than once.
 * Our goal is to record the events during transactions, but postpone validation checks
 * until the end of transaction, thus saving performance on repeat changes to an entity.
 * Example:
 * for(long loop) ex.event(null, cat, tran);
 * have same effect that the first call alone.
 */
public class IgnoreSubsequentEvents
{
    // TODO: implement
}
