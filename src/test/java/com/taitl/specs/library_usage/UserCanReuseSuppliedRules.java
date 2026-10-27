package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.handlers.access_handlers.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import com.taitl.existential.exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;

/** EventsAndConstraints.spec.md#supplied-rule-binding. */
class UserCanReuseSuppliedRules
{
    static final String OP = "/city/supplied";

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void requiredPolicyValidatesSuppliedLeavesWithoutPartialRegistration(boolean transaction)
    {
        Existential ex = new Existential();
        AtomicInteger calls = new AtomicInteger();
        OnRead<Cat> leaf = new OnRead<>(cat -> {
            calls.incrementAndGet();
            return true;
        }, null, null);
        Invariant<Cat> supplied = new Invariant<>(Cat.class).add(leaf);
        try
        {
            ex.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
            com.taitl.existential.builders.ContextBuilder context = ex.configure().context(OP);
            if (transaction)
            {
                com.taitl.existential.builders.TransactionBuilder local = context.transaction("Local");
                assertThrows(IllegalArgumentException.class, () -> local.invariant(supplied));
                leaf.description = "Corrected supplied rule";
                local.invariant(supplied);
            }
            else
            {
                assertThrows(IllegalArgumentException.class, () -> context.invariant(supplied));
                leaf.description = "Corrected supplied rule";
                context.invariant(supplied);
            }
            supplied.read(cat -> true);
            supplied.list().remove(supplied.list().size() - 1);
            Tr tr = ex.begin(OP);
            try
            {
                tr.read(new Cat("Black", "Park"));
                tr.commit();
            }
            finally
            {
                tr.close();
            }
            assertEquals(1, calls.get());
        }
        finally
        {
            ex.close();
        }
    }

    @ParameterizedTest
    @CsvSource({ "false,false", "false,true", "true,false", "true,true" })
    void suppliedIntentsAndEffectsUseAttachmentPolicy(boolean transaction, boolean intent)
    {
        Existential ex = new Existential();
        try
        {
            ex.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
            com.taitl.existential.builders.ContextBuilder context = ex.configure().context(OP);
            OnRead<Cat> leaf = intent ? new OnRead<>(cat -> true, null, null)
                    : new OnRead<>(null, cat -> {
                    }, null);
            Intent<Cat> authorization = new Intent<>(Cat.class).add(leaf);
            Effect<Cat> effect = new Effect<>(Cat.class).add(leaf);
            Runnable attach;
            if (transaction)
            {
                com.taitl.existential.builders.TransactionBuilder local = context.transaction("Local");
                attach = intent ? () -> local.intent(authorization, new TypeKey<>(Cat.class))
                        : () -> local.effect(effect);
            }
            else
            {
                attach = intent ? () -> context.intent(authorization, new TypeKey<>(Cat.class))
                        : () -> context.effect(effect);
            }
            assertThrows(IllegalArgumentException.class, () -> attach.run());
            leaf.description = "Corrected supplied leaf";
            attach.run();
            Tr tr = ex.begin(OP);
            try
            {
                tr.read(new Cat("Black", "Park"));
                tr.commit();
            }
            finally
            {
                tr.close();
            }
        }
        finally
        {
            ex.close();
        }
    }

    @Test
    void bindingIntentDoesNotRewriteDefinitionOrAnotherOwner()
    {
        Existential first = new Existential();
        Existential second = new Existential();
        Intent<Cat> supplied = new Intent<>(Cat.class).read(cat -> true, "Read permission");
        TypeKey<Cat> original = supplied.typeKey();
        try
        {
            first.configure().context(OP).intent(supplied, new TypeKey<>(Cat.class, true));
            second.configure().context(OP).transaction("Local").intent(supplied, new TypeKey<>(Cat.class));
            assertSame(original, supplied.typeKey());
            for (Existential ex : new Existential[] { first, second })
            {
                Tr tr = ex.begin(OP);
                try
                {
                    tr.read(new Cat("Black", "Park"));
                    tr.commit();
                }
                finally
                {
                    tr.close();
                }
            }
        }
        finally
        {
            first.close();
            second.close();
        }
    }

    @Test
    void readOnlyDefinitionHasIndependentOriginsAndSurvivesAnotherInstanceClosing()
    {
        Invariant<Cat> supplied = new Invariant<>(Cat.class).read(cat -> false, "Shared rule");
        Existential first = new Existential();
        Existential second = new Existential();
        try
        {
            first.configure().context(OP).invariant(supplied);
            second.configure().context(OP + "/other").invariant(supplied);
            assertOrigin(first, OP, OP);
            first.close();
            assertOrigin(second, OP + "/other", OP + "/other");
            assertEquals(1, supplied.list().size());
            assertEquals("Shared rule", ((OnRead<?>) supplied.list().get(0)).description());
        }
        finally
        {
            first.close();
            second.close();
        }
    }

    @Test
    void earlierOptionalAttachmentIsNotRetroactivelyRejected()
    {
        Existential ex = new Existential();
        try
        {
            Invariant<Cat> optional = new Invariant<>(Cat.class).read(cat -> true);
            ex.configure().context(OP).invariant(optional);
            ex.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
            Tr tr = ex.begin(OP);
            try
            {
                tr.read(new Cat("Black", "Park"));
                tr.commit();
            }
            finally
            {
                tr.close();
            }
        }
        finally
        {
            ex.close();
        }
    }

    void assertOrigin(Existential ex, String op, String context)
    {
        Tr tr = ex.begin(op);
        try
        {
            tr.read(new Cat("Black", "Park"));
            ValidationStageExceptions failure = assertThrows(ValidationStageExceptions.class, () -> tr.commit());
            assertEquals(context, failure.diagnostic().children().get(0).context());
            assertEquals(op, failure.diagnostic().operation());
            assertEquals("Shared rule", failure.diagnostic().children().get(0).rule());
        }
        finally
        {
            tr.close();
        }
    }
}
