package com.taitl.existential.constraints;

import com.taitl.existential.configs.Transaction;
import com.taitl.existential.handlers.OnCreate;
import com.taitl.existential.handlers.OnTransit;
import com.taitl.existential.keys.TypeKey;
import org.junit.jupiter.api.*;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InvariantTest
{
    static class Widget
    {
    }

    @Nested
    class Constructors
    {
        @Test
        @DisplayName("Default constructor requires anonymous subclass")
        void defaultRequiresAnonymousSubclass()
        {
            assertThrows(IllegalStateException.class, () -> new Invariant<Widget>());
        }

        @Test
        @DisplayName("Anonymous subclass infers type key")
        void anonymousInfersTypeKey()
        {
            Invariant<Widget> invariant = new Invariant<Widget>() {
            };

            assertThat(invariant.typeKey(), is(new TypeKey<>(Widget.class)));
        }
    }

    @Nested
    class Handlers
    {
        @Test
        @DisplayName("Handlers preserve descriptions")
        void preserveDescriptions()
        {
            Invariant<String> invariant = new Invariant<>(String.class);
            Predicate<String> condition = value -> value.length() > 2;
            BiPredicate<String, String> bicondition = (before, after) -> before.length() <= after.length();

            invariant.create(condition, "Must be long enough")
                    .transit(bicondition, "Length cannot shrink");

            assertThat(invariant.list().get(0), instanceOf(OnCreate.class));
            OnCreate<String> create = (OnCreate<String>) invariant.list().get(0);
            assertThat(create.description(), is("Must be long enough"));

            assertThat(invariant.list().get(1), instanceOf(OnTransit.class));
            OnTransit<String> transit = (OnTransit<String>) invariant.list().get(1);
            assertThat(transit.description(), is("Length cannot shrink"));
        }
    }

    @Nested
    class TransactionAssignment
    {
        @Test
        @DisplayName("Transaction getter requires assignment")
        void getterRequiresAssignment()
        {
            Invariant<String> invariant = new Invariant<>(String.class);

            assertThat(assertThrows(IllegalStateException.class, invariant::transaction)
                    .getMessage(), containsString("tran"));

            Transaction transaction = new Transaction("op", "name");
            invariant.transaction(transaction);
            assertThat(invariant.transaction(), is(transaction));
        }
    }
}
