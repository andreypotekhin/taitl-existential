package com.taitl.existential.quantifiers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.exceptions.PredicateFailureException;
import com.taitl.examples.night_city.model.House;
import com.taitl.examples.night_city.data.CityTestData;

import java.util.function.Predicate;

class AllTest
{
    All<House> o;
    House house;

    @BeforeEach
    void setUp() throws Exception
    {
        o = new All<>(h -> h.hasRoof());
        house = new House("Gray", CityTestData.ADDRESS_EAST_ST);
    }

    @AfterEach
    void tearDown() throws Exception
    {
        o = null;
    }

    @Test
    void constructWithPredicate()
    {
        o = new All<>(h -> h.hasRoof());
        assertNull(o.condition);
        assertNotNull(o.predicate);
        assertThrows(IllegalArgumentException.class, () -> {
            new All<>(null);
        });
    }

    @Test
    void constructWithConditionAndPredicate()
    {
        o = new All<>(h -> true, h -> h.hasRoof());
        assertNotNull(o.condition);
        assertNotNull(o.predicate);
        assertThrows(IllegalArgumentException.class, () -> {
            new All<House>(null, h -> h.hasRoof());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new All<House>(h -> true, (Predicate) null);
        });
    }

    @Test
    void constructWithDescription()
    {
        o = new All<>(h -> true, h -> h.hasRoof(), "description");
        assertNotNull(o.condition);
        assertNotNull(o.predicate);
        assertNotNull(o.description);
        assertThrows(IllegalArgumentException.class, () -> {
            new All<House>(h -> true, h -> h.hasRoof(), null);
        });
    }

    @Test
    void evaluate() throws ExistentialException
    {
        o.evaluate(house);

        o = new All<>(h -> true, h -> h.hasRoof());
        o.evaluate(house);

        // Fail on predicate (second parm) not true
        o = new All<>(h -> true, h -> !h.hasRoof());
        assertThrows(PredicateFailureException.class, () -> {
            o.evaluate(house);
        });

        // Set condition (first param) never true, so second predicate does not get evaluated
        o = new All<>(h -> false, h -> !h.hasRoof());
        o.evaluate(house);

        // Fail on passed-in object not fitting the predicate
        o = new All<>(h -> true, h -> h.hasRoof());
        house.hasRoof = false;
        assertThrows(PredicateFailureException.class, () -> {
            o.evaluate(house);
        });
    }

}
