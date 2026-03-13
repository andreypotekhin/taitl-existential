package com.taitl.existential.constraints;

import com.taitl.existential.configs.Transaction;
import com.taitl.existential.evaluables.Ev;
import com.taitl.existential.expressions.Expression;
import com.taitl.existential.handlers.OnCreate;
import com.taitl.existential.handlers.OnTransit;
import com.taitl.existential.handlers.combined_event_handlers.OnCUD;
import com.taitl.existential.handlers.combined_event_handlers.OnUD;
import com.taitl.existential.keys.TypeKey;
import org.junit.jupiter.api.*;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
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

        @Test
        @DisplayName("Description-less overloads keep empty descriptions")
        void descriptionLessOverloadsKeepEmptyDescriptions()
        {
            Invariant<String> invariant = new Invariant<>(String.class);

            invariant.create(value -> value.length() > 2)
                    .transit((before, after) -> before.length() <= after.length())
                    .all(value -> !value.isBlank());

            OnCreate<String> create = (OnCreate<String>) invariant.list().get(0);
            OnTransit<String> transit = (OnTransit<String>) invariant.list().get(1);

            assertThat(create.description(), is(""));
            assertThat(transit.description(), is(""));
            assertThat(((Expression<?>) invariant.list().get(2)).description(), is(""));
        }

        @Test
        @DisplayName("CUD and UD invariants preserve descriptions")
        void combinedHandlersPreserveDescriptions()
        {
            Invariant<String> invariant = new Invariant<>(String.class);

            invariant.cud(value -> value.length() > 2, "CUD")
                    .ud(value -> value.length() > 1, "UD");

            assertThat(invariant.list().get(0), instanceOf(OnCUD.class));
            assertThat(invariant.list().get(1), instanceOf(OnUD.class));
            assertThat(((OnCUD<String>) invariant.list().get(0)).description(), is("CUD"));
            assertThat(((OnUD<String>) invariant.list().get(1)).description(), is("UD"));
        }

        @Test
        @DisplayName("Directly added events may omit descriptions when descriptions are required")
        void directEventsMayOmitDescriptionsWhenDescriptionsAreRequired()
        {
            Invariant<String> invariant = new Invariant<>(String.class);
            Ev<String> create = new OnCreate<>(value -> {
            }, null);

            invariant.requireDescriptions(true);
            invariant.add(create);

            assertThat(invariant.list().get(0), is(create));
        }

        @Test
        @DisplayName("Requiring descriptions does not revalidate existing description-less events")
        void requiringDescriptionsDoesNotRevalidateExistingDescriptionLessEvents()
        {
            Invariant<String> invariant = new Invariant<>(String.class);

            invariant.add(new OnCreate<>(value -> {
            }, null));

            invariant.requireDescriptions(true);

            assertThat(invariant.list(), hasSize(1));
        }
    }

    @Nested
    class DescriptionValidation
    {
        @Test
        @DisplayName("Explicit description parameters are still required when enabled")
        void explicitDescriptionParametersAreStillRequiredWhenEnabled()
        {
            Invariant<String> invariant = new Invariant<>(String.class);
            invariant.requireDescriptions(true);

            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> invariant.create(value -> true, null));

            assertThat(exception.getMessage(), containsString("require descriptions"));
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
