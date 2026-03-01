package com.taitl.existential.transactions;

import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.handlers.transaction_handlers.*;
import com.taitl.existential.keys.TypeKey;
import org.junit.jupiter.api.*;

import java.util.function.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LifeTest
{
    @Test
    void defaultConstructorRequiresAnonymousSubclass()
    {
        assertThrows(IllegalStateException.class, () -> new Life<Transaction>());
    }

    @Test
    void anonymousSubclassInfersTypeKey()
    {
        Life<Transaction> life = new Life<Transaction>() {
        };

        assertThat(life.typeKey(), is(new TypeKey<>(Transaction.class)));
    }

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

    @Test
    void commitPreservesDescriptionForConditionalHandler()
    {
        Life<Transaction> life = new Life<>(Transaction.class);
        Predicate<Transaction> condition = tr -> true;
        Consumer<Transaction> action = tr -> {
        };

        life.commit(condition, action, "Commit handler description");

        OnCommit<Transaction> handler = (OnCommit<Transaction>) life.list().get(0);
        assertThat(handler.description(), is("Commit handler description"));
    }

    @Test
    void transactionGetterRequiresAssignment()
    {
        Life<Transaction> life = new Life<>(Transaction.class);

        assertThat(assertThrows(IllegalStateException.class, life::transaction)
                .getMessage(), containsString("tran"));

        Transaction transaction = new Transaction("op", "name");
        life.transaction(transaction);
        assertThat(life.transaction(), is(transaction));
    }
}
