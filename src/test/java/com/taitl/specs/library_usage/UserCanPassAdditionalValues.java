package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;

/** EventsAndConstraints.spec.md#additional-event-values: ordinary events over application envelopes. */
class UserCanPassAdditionalValues
{
    static final String OP = "/city/relocation-values";
    Existential ex = new Existential();
    List<Relocation> seen = new ArrayList<>();

    @AfterEach
    void close()
    {
        ex.close();
    }

    @ParameterizedTest
    @CsvSource({ "false,0", "false,1", "false,2", "true,0", "true,1", "true,2" })
    void envelopeReachesOrdinaryRulesWithEachEntryPoint(boolean full, int route)
    {
        if (full)
            ex.on(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
        ex.configure().context(OP).invariant(new TypeKey<Relocation>(Relocation.class, full)).read(value -> {
            seen.add(value);
            return value.actor.equals("Keeper") && value.destination.equals("Garden");
        }, "Relocation carries actor and destination");
        Relocation value = new Relocation(new Cat("Black", "Park"), "Keeper", "Garden");
        Tr tr = ex.begin(OP);
        try
        {
            if (route == 0)
                tr.read(value);
            else if (route == 1)
                ex.read(value, tr.id());
            else
                tr.read(value, new TypeKey<>(Relocation.class, full));
            tr.commit();
            assertEquals(List.of(value), seen);
            assertSame(value, seen.get(0));
            assertEquals("Park", value.cat.location());
            assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void distinctEnvelopesKeepExtraValuesWhileRepeatedIdentityFolds()
    {
        ex.configure().context(OP).invariant(Relocation.class).read(value -> {
            seen.add(value);
            return true;
        }, "Record each envelope identity");
        Cat cat = new Cat("Black", "Park");
        Relocation first = new Relocation(cat, "Keeper", "Garden");
        Relocation second = new Relocation(cat, "Guest", "Park");
        Tr tr = ex.begin(OP);
        try
        {
            tr.read(first);
            tr.read(first);
            tr.read(second);
            tr.commit();
            assertEquals(List.of(first, second), seen);
            assertSame(cat, seen.get(1).cat);
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void checkpointRetainsValuesAndFailureAllowsFreshCorrection()
    {
        AtomicInteger rollback = new AtomicInteger();
        ex.configure().context(OP).invariant(Relocation.class).read(value -> {
            seen.add(value);
            return value.actor.equals("Keeper");
        }, "Only keepers authorize relocation");
        com.taitl.existential.configs.Transaction local = new com.taitl.existential.configs.Transaction(OP, "Cleanup");
        local.rollback(transaction -> rollback.incrementAndGet());
        Tr tr = ex.begin(OP, local);
        Relocation accepted = new Relocation(new Cat("Black", "Park"), "Keeper", "Garden");
        try
        {
            tr.read(accepted);
            tr.checkpoint();
            assertEquals(List.of(accepted), seen);
            tr.read(new Relocation(accepted.cat, "Guest", "Garden"));
            ValidationStageExceptions failure = assertThrows(ValidationStageExceptions.class, () -> tr.commit());
            assertEquals("Only keepers authorize relocation", failure.diagnostic().children().get(0).rule());
            assertEquals(0, rollback.get());
            tr.rollback();
            assertEquals(1, rollback.get());
            assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
        }
        finally
        {
            tr.close();
        }
        Tr fresh = ex.begin(OP);
        try
        {
            fresh.read(accepted);
            fresh.commit();
        }
        finally
        {
            fresh.close();
        }
    }

    @Test
    void genericEnvelopeUsesItsExplicitPayloadType()
    {
        TypeKey<Command<Cat>> cats = new TypeKey<Command<Cat>>() {
        };
        AtomicInteger calls = new AtomicInteger();
        ex.configure().context(OP).invariant(cats).read(command -> {
            calls.incrementAndGet();
            return command.actor.equals("Keeper") && command.value.color.equals("Black");
        }, "Typed command payload");
        Tr tr = ex.begin(OP);
        try
        {
            tr.read(new Command<>(new Cat("Black", "Park"), "Keeper"), cats);
            tr.commit();
            assertEquals(1, calls.get());
        }
        finally
        {
            tr.close();
        }
    }

    static class Relocation
    {
        Cat cat;
        String actor;
        String destination;

        Relocation(Cat cat, String actor, String destination)
        {
            this.cat = cat;
            this.actor = actor;
            this.destination = destination;
        }
    }

    static class Command<T>
    {
        T value;
        String actor;

        Command(T value, String actor)
        {
            this.value = value;
            this.actor = actor;
        }
    }
}
