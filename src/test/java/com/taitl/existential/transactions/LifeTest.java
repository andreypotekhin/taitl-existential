package com.taitl.existential.transactions;

import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.handlers.transaction_handlers.*;
import org.junit.jupiter.api.*;

import java.util.function.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class LifeTest
{
    @Test
    void beginPreservesDescriptionForConditionalHandler()
    {
        Life<Transaction> life = new Life<>(Transaction.class);
        Predicate<Transaction> condition = tr -> true;
        Consumer<Transaction> action = tr -> {
        };

        life.begin(condition, action, "Begin handler description");

        OnBegin<Transaction> handler = (OnBegin<Transaction>) life.list().get(0);
        assertThat(handler.description(), is("Begin handler description"));
    }
}
