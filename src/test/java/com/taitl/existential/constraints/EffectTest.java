package com.taitl.existential.constraints;

import com.taitl.existential.configs.Transaction;
import com.taitl.existential.handlers.On;
import com.taitl.existential.handlers.combined_event_handlers.OnCUD;
import com.taitl.existential.handlers.combined_event_handlers.OnUD;
import com.taitl.existential.keys.TypeKey;
import org.junit.jupiter.api.*;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EffectTest
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
            assertThrows(IllegalStateException.class, () -> new Effect<Widget>());
        }

        @Test
        @DisplayName("Anonymous subclass infers type key")
        void anonymousInfersTypeKey()
        {
            Effect<Widget> effect = new Effect<Widget>() {
            };

            assertThat(effect.typeKey(), is(new TypeKey<>(Widget.class)));
        }
    }

    @Nested
    class Handlers
    {
        @Test
        @DisplayName("On handler preserves condition action and description")
        void preservesConditionActionAndDescription()
        {
            Effect<String> effect = new Effect<>(String.class);
            Predicate<String> condition = value -> value.startsWith("a");
            Consumer<String> action = value -> {
            };

            effect.on(condition, action, "Runs on a-values");

            On<String> handler = (On<String>) effect.list().get(0);
            assertThat(handler.condition, is(condition));
            assertThat(handler.action, is(action));
            assertThat(handler.description(), is("Runs on a-values"));
        }

        @Test
        @DisplayName("CUD and UD handlers are added")
        void addsCombinedEventHandlers()
        {
            Effect<String> effect = new Effect<>(String.class);

            effect.cud(value -> {
            }, "CUD")
                    .ud(value -> {
                    }, "UD");

            assertThat(effect.list().get(0).getClass(), is(OnCUD.class));
            assertThat(effect.list().get(1).getClass(), is(OnUD.class));
            assertThat(((OnCUD<String>) effect.list().get(0)).description(), is("CUD"));
            assertThat(((OnUD<String>) effect.list().get(1)).description(), is("UD"));
        }
    }

    @Nested
    class TransactionAssignment
    {
        @Test
        @DisplayName("Transaction getter requires assignment")
        void getterRequiresAssignment()
        {
            Effect<String> effect = new Effect<>(String.class);

            assertThat(assertThrows(IllegalStateException.class, effect::getTransaction)
                    .getMessage(), containsString("tran"));

            Transaction transaction = new Transaction("op", "name");
            effect.setTransaction(transaction);
            assertThat(effect.getTransaction(), is(transaction));
        }
    }
}
