package com.taitl.existential.events.types;

import com.taitl.existential.constants.Strings;
import com.taitl.existential.transactions.Transaction;

/**
 * Implements type of event related to lifecycle of a business transaction,
 * such as begin, checkpoint, commit or rollback.
 *
 * @author Andrey Potekhin
 *
 * @param <T> Transaction type
 *
 * @see BeginTran<Tr>
 */
public class TransactionEvent<T extends Transaction> implements Event<T>
{
    public T tr;

    public TransactionEvent(T tr)
    {
        if (tr == null)
        {
            throw new IllegalArgumentException(Strings.ARG_TR);
        }
        this.tr = tr;
    }
}
