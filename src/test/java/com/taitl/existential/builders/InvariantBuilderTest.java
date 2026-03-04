package com.taitl.existential.builders;

import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
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

    @Nested
    class ExistsMethods
    {
        @Test
        @DisplayName("exists(values, predicate) evaluates per value")
        void evaluatesPerValue()
        {
            InvariantBuilder<String> builder = builder();
            Exists<String> exists = builder.exists(List.of("a", "boat"), value -> value.length() > 3);

            assertTrue(exists.test(transaction()));
        }

        @Test
        @DisplayName("exists(values, bipredicate) evaluates value and transaction")
        void evaluatesValueAndTransaction()
        {
            InvariantBuilder<String> builder = builder();
            Exists<String> exists = builder.exists(List.of("a", "boat"),
                    (value, transaction) -> value.length() > 3 && transaction.name.equals("tx"));

            assertTrue(exists.test(transaction()));
            assertFalse(exists.test(new Transaction("/app", "other")));
        }

        @Test
        @DisplayName("exists(values, collection-predicate, placeholder) evaluates collection")
        void evaluatesCollection()
        {
            InvariantBuilder<String> builder = builder();
            Exists<String> exists = builder.exists(List.of("a", "boat"), values -> values.size() == 2, 0);

            assertTrue(exists.test(transaction()));
        }

        @Test
        @DisplayName("exists(values, collection-bipredicate, placeholder) evaluates collection and transaction")
        void evaluatesCollectionAndTransaction()
        {
            InvariantBuilder<String> builder = builder();
            Exists<String> exists = builder.exists(List.of("a", "boat"),
                    (values, transaction) -> values.size() == 2 && transaction.op.equals("/app"), 0);

            assertTrue(exists.test(transaction()));
            assertFalse(exists.test(new Transaction("/other", "tx")));
        }
    }

    private InvariantBuilder<String> builder()
    {
        ConfigBuilder configBuilder = new ConfigBuilder();
        return configBuilder.context("/app").invariant(String.class);
    }

    private Transaction transaction()
    {
        return new Transaction("/app", "tx");
    }
}
