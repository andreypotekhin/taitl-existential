package com.taitl.existential.constraints;

import com.taitl.existential.configs.Transaction;
import com.taitl.existential.handlers.OnCreate;
import com.taitl.existential.handlers.OnMutate;
import com.taitl.existential.keys.TypeKey;
import org.junit.jupiter.api.Test;

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

    @Test
    void defaultConstructorRequiresAnonymousSubclass()
    {
        assertThrows(IllegalStateException.class, () -> new Invariant<Widget>());
    }

    @Test
    void anonymousSubclassInfersTypeKey()
    {
        Invariant<Widget> invariant = new Invariant<Widget>() {
        };

        assertThat(invariant.typeKey(), is(new TypeKey<>(Widget.class)));
    }

    @Test
    void handlersPreserveDescriptions()
    {
        Invariant<String> invariant = new Invariant<>(String.class);
        Predicate<String> condition = value -> value.length() > 2;
        BiPredicate<String, String> bicondition = (before, after) -> before.length() <= after.length();

        invariant.create(condition, "Must be long enough")
                .mutate(bicondition, "Length cannot shrink");

        assertThat(invariant.list().get(0), instanceOf(OnCreate.class));
        OnCreate<String> create = (OnCreate<String>) invariant.list().get(0);
        assertThat(create.description(), is("Must be long enough"));

        assertThat(invariant.list().get(1), instanceOf(OnMutate.class));
        OnMutate<String> mutate = (OnMutate<String>) invariant.list().get(1);
        assertThat(mutate.description(), is("Length cannot shrink"));
    }

    @Test
    void transactionGetterRequiresAssignment()
    {
        Invariant<String> invariant = new Invariant<>(String.class);

        assertThat(assertThrows(IllegalStateException.class, invariant::transaction)
                .getMessage(), containsString("tran"));

        Transaction transaction = new Transaction("op", "name");
        invariant.transaction(transaction);
        assertThat(invariant.transaction(), is(transaction));
    }
}
