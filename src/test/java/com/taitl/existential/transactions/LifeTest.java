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
    @Nested
    class Constructors
    {
        @Test
        @DisplayName("Default constructor requires anonymous subclass")
        void defaultRequiresAnonymousSubclass()
        {
            assertThrows(IllegalStateException.class, () -> new Life<Transaction>());
        }

        @Test
        @DisplayName("Anonymous subclass infers type key")
        void anonymousInfersTypeKey()
        {
            Life<Transaction> life = new Life<Transaction>() {
            };

            assertThat(life.typeKey(), is(new TypeKey<>(Transaction.class)));
        }
    }

    @Nested
    class Handlers
    {
        @Test
        @DisplayName("Begin preserves description for conditional handler")
        void beginPreservesDescription()
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
        @DisplayName("Commit preserves description for conditional handler")
        void commitPreservesDescription()
        {
            Life<Transaction> life = new Life<>(Transaction.class);
            Predicate<Transaction> condition = tr -> true;
            Consumer<Transaction> action = tr -> {
            };

            life.commit(condition, action, "Commit handler description");

            OnCommit<Transaction> handler = (OnCommit<Transaction>) life.list().get(0);
            assertThat(handler.description(), is("Commit handler description"));
        }
    }

    @Nested
    class TransactionAssignment
    {
        @Test
        @DisplayName("Transaction getter requires assignment")
        void getterRequiresAssignment()
        {
            Life<Transaction> life = new Life<>(Transaction.class);

            assertThat(assertThrows(IllegalStateException.class, life::transaction)
                    .getMessage(), containsString("tran"));

            Transaction transaction = new Transaction("op", "name");
            life.transaction(transaction);
            assertThat(life.transaction(), is(transaction));
        }
    }
}
