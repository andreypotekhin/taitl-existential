package com.taitl.existential.transactions;

import java.util.function.Consumer;
import java.util.function.Predicate;
import com.taitl.existential.configs.Transaction;
import com.taitl.existential.handlers.transaction_handlers.OnBegin;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TrancycleTest
{
    @Test
    void beginPreservesDescriptionForConditionalHandler()
    {
        Trancycle<Transaction> trancycle = new Trancycle<>();
        Predicate<Transaction> condition = tr -> true;
        Consumer<Transaction> action = tr -> {
        };

        trancycle.begin(condition, action, "Begin handler description");

        OnBegin<Transaction> handler = (OnBegin<Transaction>) trancycle.list().get(0);
        assertThat(handler.description(), is("Begin handler description"));
    }
}
