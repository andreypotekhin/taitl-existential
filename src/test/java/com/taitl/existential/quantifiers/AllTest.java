package com.taitl.existential.quantifiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.*;

import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.exceptions.PredicateFailure;
import com.taitl.ex.examples.night_city.model.House;
import com.taitl.ex.examples.night_city.data.CityTestData;

import java.util.function.Predicate;

class AllTest
{
    All<House> o;
    House house;

    @BeforeEach
    void setUp()
    {
        o = new All<>(h -> h.hasRoof());
        house = new House("Gray", CityTestData.ADDRESS_EAST_ST);
    }

    @Nested
    class Constructors
    {
        @Test
        @DisplayName("Construct with predicate")
        void withPredicate()
        {
            o = new All<>(h -> h.hasRoof());
            assertNotNull(o.concrete);
            assertEquals("", o.description());
            assertThrows(IllegalArgumentException.class, () -> {
                new All<>(null);
            });
        }

        @Test
        @DisplayName("Construct with condition and predicate")
        void withConditionAndPredicate()
        {
            o = new All<>(h -> true, h -> h.hasRoof());
            assertNotNull(o.concrete);
            assertThrows(IllegalArgumentException.class, () -> {
                new All<House>(null, h -> h.hasRoof());
            });
            assertThrows(IllegalArgumentException.class, () -> {
                new All<House>(h -> true, (Predicate) null);
            });
        }

        @Test
        @DisplayName("Construct with description")
        void withDescription()
        {
            o = new All<>(h -> true, h -> h.hasRoof(), "description");
            assertNotNull(o.concrete);
            assertNotNull(o.description());
            assertThrows(IllegalArgumentException.class, () -> {
                new All<House>(h -> true, h -> h.hasRoof(), null);
            });
        }
    }

    @Nested
    class Evaluate
    {
        @Test
        void evaluate() throws ExistentialException
        {
            o.evaluate(house);

            o = new All<>(h -> true, h -> h.hasRoof());
            o.evaluate(house);

            // Fail on predicate (second parm) not true
            o = new All<>(h -> true, h -> !h.hasRoof());
            assertThrows(PredicateFailure.class, () -> {
                o.evaluate(house);
            });

            // Set condition (first param) never true, so second predicate does not get evaluated
            o = new All<>(h -> false, h -> !h.hasRoof());
            o.evaluate(house);

            // Fail on passed-in object not fitting the predicate
            o = new All<>(h -> true, h -> h.hasRoof());
            house.hasRoof = false;
            assertThrows(PredicateFailure.class, () -> {
                o.evaluate(house);
            });
        }
    }

}
