package com.taitl.existential.builders;

import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.quantifiers.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InvariantBuilderTest
{
    @Nested
    class AllMethods
    {
        @Test
        @DisplayName("all(predicate, description) adds all expression")
        void addsAllExpression()
        {
            InvariantBuilder<String> builder = builder();

            InvariantBuilder<String> chained = builder.all(value -> !value.isBlank(), "Value must not be blank");
            Invariant<String> invariant = builder.build();

            assertSame(builder, chained);
            assertEquals(1, invariant.list().size());
            All<?> expression = assertInstanceOf(All.class, invariant.list().get(0));
            assertEquals("Value must not be blank", expression.description());
        }

        @Test
        @DisplayName("all(condition, predicate, description) adds all expression")
        void addsConditionalAllExpression()
        {
            InvariantBuilder<String> builder = builder();

            builder.all(value -> value.startsWith("a"), value -> value.length() > 1, "A values must be 2+ chars");
            Invariant<String> invariant = builder.build();

            assertEquals(1, invariant.list().size());
            All<?> expression = assertInstanceOf(All.class, invariant.list().get(0));
            assertEquals("A values must be 2+ chars", expression.description());
        }
    }

    public static <T, K> K findFirst(Evs<T> evs, Class<K> clazz)
    {
        for (Ev<?> ev : evs.list())
        {
            if (clazz.isInstance(ev))
            {
                return clazz.cast(ev);
            }
        }
        throw new IllegalStateException("No expression of type " + clazz.getSimpleName() + " found.");
    }

    @Nested
    class ExistsMethods
    {
        @Test
        @DisplayName("exists(values) evaluates per value")
        void evaluatesPerValue()
        {
            InvariantBuilder<String> builder = builder();
            Invariant<String> invariant = builder.exists(Set.of("a", "boat")).build();
            Exists<String> exists = findFirst(invariant, Exists.class);
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("goat"));
        }

        @Test
        @DisplayName("exists(values, predicate) evaluates per value")
        void evaluatesPerValueAndPredicate()
        {
            InvariantBuilder<String> builder = builder();
            Invariant<String> invariant = builder.exists(Set.of("a", "boat"), value -> value.length() > 3).build();
            Exists<String> exists = findFirst(invariant, Exists.class);
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("a"));
        }

        @Test
        @DisplayName("exists(values, predicate, description) stores description")
        void storesPredicateDescription()
        {
            InvariantBuilder<String> builder = builder();
            Invariant<String> invariant = builder.exists(Set.of("a", "boat"), value -> value.length() > 3,
                    "At least one long value must exist").build();
            Exists<String> exists = findFirst(invariant, Exists.class);
            assertEquals("At least one long value must exist", exists.description());
        }

        @Test
        @DisplayName("exists(map) evaluates keys")
        void evaluatesMapKeys()
        {
            InvariantBuilder<String> builder = builder();
            Map<String, Integer> values = Map.of("a", 1, "boat", 2);
            Invariant<String> invariant = builder.exists(values).build();
            Exists<String> exists = findFirst(invariant, Exists.class);
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("goat"));
        }

        @Test
        @DisplayName("exists(map, bipredicate) evaluates key and map value")
        void evaluatesMapKeyAndValue()
        {
            InvariantBuilder<String> builder = builder();
            Map<String, Integer> values = Map.of("a", 1, "boat", 2);
            Invariant<String> invariant = builder.exists(values, (key, value) -> key.length() > value).build();
            Exists<String> exists = findFirst(invariant, Exists.class);
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("a"));
        }

        @Test
        @DisplayName("exists(values, bipredicate) evaluates entity and matched value")
        void evaluatesEntityAndMatchedValue()
        {
            InvariantBuilder<String> builder = builder();
            Invariant<String> invariant = builder.exists(Set.of(new String("boat")),
                    (entity, matched) -> entity != matched && entity.equals(matched)).build();
            Exists<String> exists = findFirst(invariant, Exists.class);
            assertTrue(exists.test(new String("boat")));
            assertFalse(exists.test("goat"));
        }

        @Test
        @DisplayName("exists(values, set-predicate, placeholder) evaluates set")
        void evaluatesSet()
        {
            InvariantBuilder<String> builder = builder();
            Invariant<String> invariant = builder.exists(Set.of("a", "boat"),
                    (Set<String> values) -> values.size() == 1, "set", "").build();
            Exists<String> exists = findFirst(invariant, Exists.class);
            assertTrue(exists.test("boat"));
        }

        @Test
        @DisplayName("exists(values, set-bipredicate, placeholder) evaluates entity and matches")
        void evaluatesEntityAndMatches()
        {
            InvariantBuilder<String> builder = builder();
            Invariant<String> invariant = builder.exists(Set.of("a", "boat"),
                    (String entity, Set<String> matches) -> matches.size() == 1 && matches.contains(entity), "set",
                    "")
                    .build();
            Exists<String> exists = findFirst(invariant, Exists.class);
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("goat"));
        }

        @Test
        @DisplayName("exists(collection, collection-predicate, placeholder) preserves duplicate matches")
        void preservesDuplicateCollectionMatches()
        {
            InvariantBuilder<String> builder = builder();
            Invariant<String> invariant = builder.exists(List.of("boat", "boat"), values -> values.size() == 2, 0)
                    .build();
            Exists<String> exists = findFirst(invariant, Exists.class);
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("goat"));
        }

        @Test
        @DisplayName("exists(map, collection-bipredicate, placeholder) evaluates entity and key matches")
        void evaluatesEntityAndMapKeyMatches()
        {
            InvariantBuilder<String> builder = builder();
            Map<String, Integer> values = Map.of("a", 1, "boat", 2);
            Invariant<String> invariant = builder.exists(values,
                    (entity, matches) -> matches.size() == 1 && matches.contains(entity), 0).build();
            Exists<String> exists = findFirst(invariant, Exists.class);
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("goat"));
        }

        // @Test
        // @DisplayName("exists(values, collection-bipredicate, placeholder) evaluates collection
        // and transaction")
        // void evaluatesCollectionAndTransaction()
        // {
        // InvariantBuilder<String> builder = builder();
        // Invariant<String> invariant = builder.exists(List.of("a", "boat"),
        // (values, transaction) -> values.size() == 2 && transaction.op.equals("/app"), 0).build();
        // Exists<String> exists = findFirst(invariant, Exists.class);
        // assertTrue(exists.test(transaction()));
        // assertFalse(exists.test(new Transaction("/other", "tx")));
        // }
    }

    private InvariantBuilder<String> builder()
    {
        ConfigBuilder configBuilder = new ConfigBuilder();
        return configBuilder.context("/app").invariant(String.class);
    }

}
