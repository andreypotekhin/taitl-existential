package com.taitl.existential.handlers;

import com.taitl.existential.exceptions.EventHandlerFailureException;
import com.taitl.existential.expressions.All;
import com.taitl.existential.model.cats.Cat;
import com.taitl.existential.model.cats.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class OnTest
{

    On<Cat> on;
    Cat cat;

    @BeforeEach
    void setup()
    {
        on = new On<>(c -> "Black".equals(c.color));
        cat = new Cat(TestData.BLACK_CAT.color(), TestData.BLACK_CAT.location());
    }

    @Test
    void handleExpression() throws Exception
    {
        cat.color = "White";
        on = new On<>(c -> "Black".equals(c.color), null, "Cats are black");
        assertThat(assertThrows(EventHandlerFailureException.class, () -> {
            on.handle(cat);
        }).getMessage(), containsString("Cats are black"));
        assertThat(cat.color, is("White"));
    }

    @Test
    void handleSideEffect() throws Exception
    {
        assertThat(cat.color, is("Black"));
        on = new On<>(c -> c.color = "White");
        on.handle(cat);
        assertThat(cat.color, is("White"));
    }

    @Test
    void description()
    {
        on = new On<>(c -> "Black".equals(c.color), "Cats are black");
        assertThat(on.description(), is("Cats are black"));
    }
}