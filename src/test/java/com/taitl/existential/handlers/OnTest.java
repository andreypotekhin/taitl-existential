package com.taitl.existential.handlers;

import com.taitl.existential.exceptions.*;
import com.taitl.examples.night_city.model.Cat;
import com.taitl.examples.night_city.data.CityTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        cat = new Cat(CityTestData.BLACK_CAT.color(), CityTestData.BLACK_CAT.location());
    }

    @Test
    void handleExpression() throws Exception
    {
        cat.color = "White";
        on = new On<>(c -> "Black".equals(c.color), null, "Cats are black");
        assertThat(assertThrows(ConditionNotMetException.class, () -> {
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