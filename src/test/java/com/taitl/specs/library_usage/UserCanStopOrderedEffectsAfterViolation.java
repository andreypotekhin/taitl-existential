package com.taitl.specs.library_usage;

import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanStopOrderedEffectsAfterViolation
{
    private static final String OP = "/api/effect-order";

    private Existential ex;

    @BeforeEach
    void setUp()
    {
        ex = new Existential();
    }

    @AfterEach
    void tearDown()
    {
        ex.close();
    }

    @Test
    void successfulChecksAndEffectsKeepDeclarationOrder()
    {
        List<String> order = new ArrayList<>();
        Invariant<String> first = new Invariant<>(String.class);
        first.create(value -> {
            order.add("check A");
            return true;
        }, "check A");
        Effect<String> effect = new Effect<>(String.class);
        effect.create(value -> order.add("effect B"));
        Invariant<String> last = new Invariant<>(String.class);
        last.create(value -> {
            order.add("check C");
            return true;
        }, "check C");
        // @formatter:off
        ex.configure()
                .context(OP)
                    .invariant(first)
                    .effect(effect)
                    .invariant(last);
        // @formatter:on

        Tr tr = ex.begin(OP);
        tr.create("value");
        tr.commit();
        assertEquals(List.of("check A", "effect B", "check C"), order);
    }

    @Test
    void firstOrdinaryViolationStopsEffectsAndLaterEntities()
    {
        List<String> effects = new ArrayList<>();
        Invariant<String> invariant = new Invariant<>(String.class);
        invariant.create(value -> false, "reject all values");
        Effect<String> effect = new Effect<>(String.class);
        effect.create(value -> effects.add(value));
        // @formatter:off
        ex.configure()
                .context(OP)
                    .invariant(invariant)
                    .effect(effect);
        // @formatter:on

        Tr tr = ex.begin(OP);
        tr.create("first");
        tr.create("second");
        ExistentialExceptions failure = assertThrows(ExistentialExceptions.class, tr::commit);
        assertEquals(1, failure.multiple().length);
        assertTrue(effects.isEmpty(), "the failed check precedes the effect and stops the later entity");
        tr.rollback();
    }

    @Test
    void effectAlreadyPerformedRemainsWhenLaterCheckFails()
    {
        List<String> order = new ArrayList<>();
        Invariant<String> first = new Invariant<>(String.class);
        first.create(value -> {
            order.add("check A");
            return true;
        }, "check A");
        Effect<String> effect = new Effect<>(String.class);
        effect.create(value -> order.add("effect B"));
        Invariant<String> last = new Invariant<>(String.class);
        last.create(value -> {
            order.add("check C");
            return false;
        }, "check C");
        // @formatter:off
        ex.configure()
                .context(OP)
                    .invariant(first)
                    .effect(effect)
                    .invariant(last);
        // @formatter:on

        Tr tr = ex.begin(OP);
        tr.create("value");
        assertThrows(ExistentialExceptions.class, tr::commit);
        assertEquals(List.of("check A", "effect B", "check C"), order);
        tr.rollback();
    }

    @Test
    void conditionalAndTransactionLocalEffectsParticipateInEarlyStop()
    {
        List<String> effects = new ArrayList<>();
        Invariant<String> invariant = new Invariant<>(String.class);
        invariant.create(value -> false, "reject before effect");
        Transaction local = new Transaction(OP, "local effect");
        local.effect(new Effect<>(String.class).create(value -> value.startsWith("run-"), value -> effects.add(value)));
        // @formatter:off
        ex.configure()
                .context(OP)
                    .invariant(invariant);
        // @formatter:on
        Tr tr = ex.begin(OP, local);
        tr.create("run-first");
        tr.create("run-second");
        assertThrows(ExistentialExceptions.class, tr::commit);
        assertTrue(effects.isEmpty(), "a conditional local effect does not run after an earlier violation");
        tr.rollback();
    }

    @Test
    void checksOnlyContinueCollectingViolationsAcrossEntities()
    {
        Invariant<String> invariant = new Invariant<>(String.class);
        invariant.create(value -> false, "reject all values");
        // @formatter:off
        ex.configure()
                .context(OP)
                    .invariant(invariant);
        // @formatter:on

        Tr tr = ex.begin(OP);
        tr.create("first");
        tr.create("second");
        ExistentialExceptions failure = assertThrows(ExistentialExceptions.class, tr::commit);
        assertEquals(2, failure.multiple().length);
        tr.rollback();
    }

    @Test
    void effectInAnotherStageDoesNotStopValidationCollection()
    {
        List<String> effects = new ArrayList<>();
        Invariant<String> invariant = new Invariant<>(String.class);
        invariant.create(value -> false, "reject all values");
        Effect<String> effect = new Effect<>(String.class);
        effect.create(value -> effects.add(value));
        // @formatter:off
        ex.configure()
                .context(OP)
                    .immediate()
                        .effect(effect);
        ex.configure()
                .context(OP)
                    .invariant(invariant);
        // @formatter:on

        Tr tr = ex.begin(OP);
        tr.create("first");
        tr.create("second");
        ExistentialExceptions failure = assertThrows(ExistentialExceptions.class, tr::commit);
        assertEquals(2, failure.multiple().length);
        assertEquals(List.of("first", "second"), effects);
        tr.rollback();
    }
}
