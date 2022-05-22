package com.taitl.existential.event.types;

import com.taitl.existential.constants.Strings;
import com.taitl.existential.transactions.Transaction;

/**
 * Implements type of event related to lifecycle of a business transaction,
 * such as begin, checkpoint, commit or rollback.
 *
 * @author Andrey Potekhin
 *
 * @param <Tr> Transaction type
 *
 * @see BeginTran<Tr>
 */
public class TransactionEvent<Tr extends Transaction> implements Event<Tr>
{
    public Tr tr;

    public TransactionEvent(Tr tr)
    {
        if (tr == null)
        {
            throw new IllegalArgumentException(Strings.ARG_TR);
        }
        this.tr = tr;
    }
}
