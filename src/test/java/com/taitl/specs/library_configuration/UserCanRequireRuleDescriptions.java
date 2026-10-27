package com.taitl.specs.library_configuration;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import static org.junit.jupiter.api.Assertions.*;

class UserCanRequireRuleDescriptions
{
    static final String OP = "/city/cats/describe";

    Consumer<String> declaration(Existential ex, Rule rule, boolean transaction, Cat cat, AtomicInteger calls,
            AtomicBoolean accepted)
    {
        ContextBuilder context = ex.configure().context(OP);
        IndexDefinition<MultiJoin<Cat, Cat, String>> related = IndexDefinition.multiJoin(
                "related", Cat.class, Cat.class, value -> value.location(), value -> value.location());
        if (rule == Rule.MANAGED_ALL || rule == Rule.MANAGED_EXISTS)
        {
            context.index(related);
        }
        TransactionBuilder local = transaction ? context.transaction("Described cat rules") : null;
        if (rule == Rule.INTENT)
        {
            IntentBuilder<Cat> target = transaction ? local.intent(Cat.class) : context.intent(Cat.class);
            return description -> target.create(value -> {
                calls.incrementAndGet();
                return accepted.get();
            }, description);
        }
        if (rule == Rule.EFFECT)
        {
            EffectBuilder<Cat> target = transaction ? local.effect(Cat.class) : context.effect(Cat.class);
            return description -> {
                if (description == null)
                {
                    target.create(value -> calls.incrementAndGet());
                }
                else
                {
                    target.create(value -> calls.incrementAndGet(), description);
                }
            };
        }
        InvariantBuilder<Cat> target = transaction ? local.invariant(Cat.class) : context.invariant(Cat.class);
        Predicate<Cat> predicate = value -> {
            calls.incrementAndGet();
            return accepted.get();
        };
        switch (rule)
        {
        case ALL:
            return description -> target.all(predicate, description);
        case EXISTS:
            return description -> target.exists(List.of(cat), predicate, description);
        case MANAGED_ALL:
            return description -> target.all(related, (value, join) -> {
                calls.incrementAndGet();
                return accepted.get() && join.containsLeft(value);
            }, description);
        case MANAGED_EXISTS:
            return description -> target.exists(related, join -> {
                calls.incrementAndGet();
                return accepted.get() ? join.left() : Collections.<Cat, Set<Cat>> emptyMap();
            }, description);
        default:
            return description -> target.create(predicate, description);
        }
    }

    static List<Arguments> scopes()
    {
        List<Arguments> scopes = new ArrayList<>();
        for (Rule rule : Rule.values())
        {
            scopes.add(Arguments.of(rule, false));
            scopes.add(Arguments.of(rule, true));
        }
        return scopes;
    }

    @ParameterizedTest(name = "{0}, transaction builder={1}")
    @MethodSource("scopes")
    @DisplayName("T10262602: existing builders use prospective description requirements")
    void flagsAreProspectiveEvenForAnExistingBuilder(Rule rule, boolean transaction)
    {
        try (Existential ex = new Existential())
        {
            Cat cat = new Cat("Black", "Park");
            AtomicInteger calls = new AtomicInteger();
            Consumer<String> declare = declaration(ex, rule, transaction, cat, calls, new AtomicBoolean(true));
            declare.accept(null);
            ex.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
            IllegalArgumentException failure = assertThrows(IllegalArgumentException.class, () -> declare.accept(null));
            assertTrue(failure.getMessage().contains("require descriptions"));
            assertTrue(failure.getMessage().contains("/Troubleshooting.md#required-rule-descriptions"));
            assertThrows(IllegalArgumentException.class, () -> declare.accept(""));
            declare.accept("The cat rule is documented");
            ex.off(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
            declare.accept(null);
            Tr tr = ex.begin(OP);
            try
            {
                tr.create(cat);
                tr.commit();
                assertEquals(3, calls.get(), "failed declarations do not add callbacks");
                assertThrows(IllegalStateException.class, () -> declare.accept("Too late"));
                assertThrows(IllegalStateException.class, () -> ex.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS));
            }
            finally
            {
                tr.close();
            }
        }
    }

    @ParameterizedTest(name = "{0}, transaction builder={1}")
    @MethodSource("scopes")
    @DisplayName("T10262602: required rule descriptions reject omission and permit correction")
    void requiredDescriptionsRejectMissingTextAndAllowCorrection(Rule rule, boolean transaction)
    {
        try (Existential ex = new Existential())
        {
            ex.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
            Cat cat = new Cat("Black", "Park");
            AtomicInteger calls = new AtomicInteger();
            Consumer<String> declare = declaration(ex, rule, transaction, cat, calls, new AtomicBoolean(true));
            assertThrows(IllegalArgumentException.class, () -> declare.accept(null));
            assertThrows(IllegalArgumentException.class, () -> declare.accept(""));
            declare.accept("A documented rule");
            Tr tr = ex.begin(OP);
            try
            {
                tr.create(cat);
                tr.commit();
                assertEquals(1, calls.get());
            }
            finally
            {
                tr.close();
            }
        }
    }

    @ParameterizedTest
    @EnumSource(value = Rule.class, names = { "CHECK", "ALL", "EXISTS", "MANAGED_ALL", "MANAGED_EXISTS" })
    void validationRetainsTheSuppliedDescription(Rule rule)
    {
        try (Existential ex = new Existential())
        {
            ex.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
            Cat cat = new Cat("Black", "Park");
            Consumer<String> declare = declaration(ex, rule, false, cat, new AtomicInteger(), new AtomicBoolean(false));
            declare.accept("The specific cat rule was violated");
            Tr tr = ex.begin(OP);
            try
            {
                tr.create(cat);
                ValidationStageExceptions failure = assertThrows(ValidationStageExceptions.class, () -> tr.commit());
                assertEquals(1, failure.multiple().length);
                assertTrue(failure.getCause().getMessage().contains("The specific cat rule was violated"));
                tr.rollback();
            }
            finally
            {
                tr.close();
            }
        }
    }

    enum Rule
    {
        CHECK, INTENT, EFFECT, ALL, EXISTS, MANAGED_ALL, MANAGED_EXISTS
    }
}
