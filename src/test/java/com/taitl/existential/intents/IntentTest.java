package com.taitl.existential.intents;

import com.taitl.existential.configs.Transaction;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.Ev;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.access_handlers.*;
import com.taitl.existential.handlers.combined_event_handlers.OnCU;
import com.taitl.existential.keys.TypeKey;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class IntentTest
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
            assertThrows(IllegalStateException.class, () -> new Intent<Widget>());
        }

        @Test
        @DisplayName("Anonymous subclass infers type key")
        void anonymousInfersTypeKey()
        {
            Intent<Widget> intent = new Intent<Widget>() {
            };

            assertThat(intent.typeKey(), is(new TypeKey<>(Widget.class)));
        }
    }

    @Nested
    class FluentApi
    {
        @Test
        @DisplayName("Fluent intents append handlers in order")
        void appendsHandlersInOrder()
        {
            Intent<String> intent = new Intent<>(String.class);

            intent.create()
                    .read()
                    .update()
                    .upsert()
                    .on();

            List<Ev<String>> evs = intent.list();

            assertThat(evs, hasSize(5));
            assertThat(evs.get(0), instanceOf(OnCreate.class));
            assertThat(evs.get(1), instanceOf(OnRead.class));
            assertThat(evs.get(2), instanceOf(OnUpdate.class));
            assertThat(evs.get(3), instanceOf(OnCU.class));
            assertThat(evs.get(4), instanceOf(On.class));
        }
    }

    @Nested
    class Assignment
    {
        @Test
        @DisplayName("Transaction and type key can be replaced")
        void transactionAndTypeKeyCanBeReplaced()
        {
            Intent<String> intent = new Intent<>(String.class);
            Transaction transaction = new Transaction("op", "name");
            TypeKey<String> typeKey = new TypeKey<>(String.class);

            intent.transaction(transaction);
            intent.typeKey(typeKey);

            assertThat(intent.transaction(), is(transaction));
            assertThat(intent.typeKey(), is(typeKey));
        }
    }

    @Nested
    class Handlers
    {
        @Test
        @DisplayName("Read with description preserves condition")
        void readPreservesCondition()
        {
            Intent<String> intent = new Intent<>(String.class);
            Predicate<String> condition = value -> value.startsWith("R");

            intent.read(condition, "Read must start with R");

            OnRead<String> handler = (OnRead<String>) intent.list().get(0);
            assertThat(handler.condition, is(condition));
            assertThat(handler.description(), is("Read must start with R"));
        }
    }
}
