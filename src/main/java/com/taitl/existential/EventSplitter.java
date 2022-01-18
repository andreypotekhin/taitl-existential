package com.taitl.existential;

import javax.naming.Context;

import com.taitl.existential.transactions.Transaction;

/**
 * Splits events so that various event aspects may be handled separately.
 *  
 * For example, splits one Permutation<House> event into this sequence: 
 *   On<House>
 *   Mutation<House>
 *   Permutation<House>
 *   
 * Moreover, depending on permutation:
 *   Created: Create<House>, Update<House>, Permutate<House>
 *   Updated: Update<House>, Mutate<House>, Permutate<House>
 *   Deleted: Delete<House>, Permutate<House>
 *   
 * Customizing
 *   EventSplitter can be customized per context.
 *   // Create custom event splitter class
 *   class CustomEventSplitter extends EventSplitter...
 *   // Install custom event splitter into a Context
 *   context.eventSplitterFactory(() -> new CustomEventSplitter());
 * 
 * @author Andrey Potekhin
 * @see Context
 * @see Transaction
 *
 */
public class EventSplitter
{
    public EventSplitter()
    {
        // TODO: implement this
    }
}
