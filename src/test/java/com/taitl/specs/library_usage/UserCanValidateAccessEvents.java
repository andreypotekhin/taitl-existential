package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;

class UserCanValidateAccessEvents
{
    static final String OP = "/city/cats/access";

    void configureStages(Existential ex, Map<String, Integer> calls)
    {
        TypeKey<Cat> type = new TypeKey<>(Cat.class, ex.get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES));
        // @formatter:off
        ex.configure().context(OP)
                .begin().invariant(new Invariant<>(type).on(cat -> count(calls, "begin")))
                .immediate().invariant(new Invariant<>(type).on(cat -> count(calls, "immediate")))
                .checkpoint().invariant(new Invariant<>(type).on(cat -> count(calls, "checkpoint")))
                .commit().invariant(new Invariant<>(type).on(cat -> count(calls, "commit")))
                .validation().invariant(new Invariant<>(type)
                    .on(cat -> count(calls, "validation"))
                    .read(cat -> count(calls, "read"))
                    .rl(cat -> count(calls, "rl"))
                    .write(cat -> count(calls, "write"))
                    .create(cat -> count(calls, "mutation"))
                    .update(cat -> count(calls, "mutation"))
                    .delete(cat -> count(calls, "mutation")));
        // @formatter:on
    }

    boolean count(Map<String, Integer> calls, String key)
    {
        calls.merge(key, 1, (before, added) -> before + added);
        return true;
    }

    static List<Arguments> selectionModes()
    {
        List<Arguments> modes = new ArrayList<>();
        for (Report report : Report.values())
        {
            for (boolean full : new boolean[] { false, true })
            {
                for (boolean disabled : new boolean[] { false, true })
                {
                    modes.add(Arguments.of(report, full, disabled));
                }
            }
        }
        return modes;
    }

    static List<Arguments> enrollmentModes()
    {
        List<Arguments> modes = new ArrayList<>();
        for (Report report : Report.values())
        {
            for (boolean automatic : new boolean[] { false, true })
            {
                for (boolean reads : new boolean[] { false, true })
                {
                    modes.add(Arguments.of(report, automatic, reads));
                }
            }
        }
        return modes;
    }

    static List<Arguments> boundaries()
    {
        List<Arguments> modes = new ArrayList<>();
        for (Report report : Report.values())
        {
            modes.add(Arguments.of(report, false));
            modes.add(Arguments.of(report, true));
        }
        return modes;
    }

    @ParameterizedTest(name = "{0}, full names={1}, elementary expansion disabled={2}")
    @MethodSource("selectionModes")
    void preservesSelectionAndStageFrequencyAcrossCheckpoint(Report report, boolean full, boolean disabled)
    {
        try (Existential ex = new Existential())
        {
            if (full)
            {
                ex.on(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
            }
            else
            {
                ex.off(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
            }
            if (disabled)
            {
                ex.on(Flags.EVENT_SPLIT_DISABLE_ELEMENTARY_TO_COMPOUND);
            }
            else
            {
                ex.off(Flags.EVENT_SPLIT_DISABLE_ELEMENTARY_TO_COMPOUND);
            }
            Map<String, Integer> calls = new HashMap<>();
            configureStages(ex, calls);
            Tr tr = ex.begin(OP);
            try
            {
                Cat cat = new Cat("Black", "Park");
                report.send(ex, tr, cat, false);
                report.send(ex, tr, cat, true);
                assertEquals(Map.of("begin", 1, "immediate", 2), calls);
                tr.checkpoint();
                assertEquals(1, calls.get("validation"));
                assertEquals(1, calls.get("checkpoint"));
                Cat equal = new Cat("Black", "Park");
                assertEquals(cat, equal);
                report.send(ex, tr, cat, false);
                report.send(ex, tr, equal, true);
                assertEquals(1, calls.get("begin"));
                assertEquals(4, calls.get("immediate"));
                tr.commit();
                assertEquals(3, calls.get("validation"));
                assertEquals(2, calls.get("commit"));
                assertEquals(report == Report.READ || report == Report.READ_LOCK ? 3 : 0,
                        calls.getOrDefault("read", 0));
                assertEquals(report == Report.READ_LOCK ? 3 : 0, calls.getOrDefault("rl", 0));
                assertEquals(report == Report.WRITE ? 3 : 0, calls.getOrDefault("write", 0));
                assertEquals(0, calls.getOrDefault("mutation", 0));
                assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
            }
            finally
            {
                tr.close();
            }
        }
    }

    @ParameterizedTest
    @EnumSource(Report.class)
    void deniedChildAndLocalIntentsDoNotEnrollCandidates(Report report)
    {
        IndexDefinition<MultiIndex<String, Cat>> cats = IndexDefinition.multi("cats", Cat.class, cat -> cat.location());
        try (Existential ex = new Existential())
        {
            // @formatter:off
            ex.configure().context("/city").intent(Cat.class).on(cat -> true, "Parent permits access");
            ex.configure().context(OP).index(cats).intent(Cat.class)
                    .on(cat -> "Black".equals(cat.color), "Child narrows access");
            // @formatter:on
            Transaction local = new Transaction(OP, "Local access restriction");
            local.intent(new Intent<>(Cat.class).on(cat -> "Garden".equals(cat.location()), "Local narrows access"));
            Tr tr = ex.begin(OP, local);
            try
            {
                assertThrows(IntentViolation.class, () -> report.send(ex, tr, new Cat("Grey", "Garden"), false));
                assertThrows(IntentViolation.class, () -> report.send(ex, tr, new Cat("Black", "Park"), true));
                assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
                assertTrue(tr.index(cats).isEmpty());
                Cat allowed = new Cat("Black", "Garden");
                report.send(ex, tr, allowed, true);
                tr.checkpoint();
                assertEquals(Set.of(allowed), tr.index(cats).get("Garden"));
                report.send(ex, tr, allowed, false);
                tr.commit();
            }
            finally
            {
                tr.close();
            }
        }
    }

    @ParameterizedTest(name = "{0}, automatic={1}, enroll reads={2}")
    @MethodSource("enrollmentModes")
    void readOptOutPreservesWriteAndExplicitMaintenance(Report report, boolean automatic, boolean reads)
    {
        AtomicInteger extractions = new AtomicInteger();
        IndexDefinition<MultiIndex<String, Cat>> cats = IndexDefinition.multi("cats", Cat.class, cat -> {
            extractions.incrementAndGet();
            return cat.location();
        });
        try (Existential ex = new Existential())
        {
            ex.configure()
                    .context(OP)
                    .index(cats)
                    .indexingPolicy(IndexingPolicy.inherit().automatic(automatic).enrollReads(reads));
            Tr tr = ex.begin(OP);
            try
            {
                Cat cat = new Cat("Black", "Park");
                MultiIndex<String, Cat> index = tr.index(cats);
                boolean maintained = automatic && (reads || report == Report.WRITE);
                report.send(ex, tr, cat, false);
                report.send(ex, tr, cat, true);
                assertEquals(maintained ? 1 : 0, index.size());
                assertEquals(maintained ? 1 : 0, extractions.get());
                if (!maintained)
                {
                    index.add(cat);
                }
                cat.location = new Location("Garden");
                report.send(ex, tr, cat, true);
                tr.checkpoint();
                assertEquals(Set.of(cat), index.get(maintained ? "Garden" : "Park"));
                assertNull(index.get(maintained ? "Park" : "Garden"));
                tr.commit();
            }
            finally
            {
                tr.close();
            }
        }
    }

    @ParameterizedTest(name = "{0}, checkpoint failure={1}")
    @MethodSource("boundaries")
    void failureRequiresExplicitCleanupAndAllowsFreshAttempt(Report report, boolean checkpoint)
    {
        IndexDefinition<MultiIndex<String, Cat>> cats = IndexDefinition.multi("cats", Cat.class, cat -> cat.location());
        try (Existential ex = new Existential())
        {
            ex.configure()
                    .context(OP)
                    .index(cats)
                    .invariant(Cat.class)
                    .all(cat -> "Black".equals(cat.color), "Only black cats may pass validation");
            AtomicInteger rollbacks = new AtomicInteger();
            Transaction local = new Transaction(OP, "Explicit rollback callback");
            local.rollback(tr -> rollbacks.incrementAndGet());
            Tr tr = ex.begin(OP, local);
            MultiIndex<String, Cat> view = tr.index(cats);
            try
            {
                report.send(ex, tr, new Cat("Grey", "Park"), false);
                assertThrows(ValidationStageExceptions.class, () -> {
                    if (checkpoint)
                    {
                        tr.checkpoint();
                    }
                    else
                    {
                        tr.commit();
                    }
                });
                assertEquals(0, rollbacks.get());
                assertThrows(NotFoundException.class, () -> ex.transactions().tr(tr.id()));
                assertThrows(ExistentialException.class, () -> report.send(ex, tr, new Cat("Black", "Park"), false));
                assertThrows(NotFoundException.class, () -> report.send(ex, tr, new Cat("Black", "Park"), true));
                assertThrows(ExistentialException.class, () -> tr.checkpoint());
                assertThrows(ExistentialException.class, () -> tr.commit());
                assertThrows(ExistentialException.class, () -> view.size());
                tr.rollback();
                assertThrows(ExistentialException.class, () -> tr.rollback());
                assertEquals(1, rollbacks.get());
                assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
            }
            finally
            {
                tr.close();
            }
            Tr fresh = ex.begin(OP);
            try
            {
                report.send(ex, fresh, new Cat("Black", "Park"), true);
                fresh.checkpoint();
                fresh.commit();
            }
            finally
            {
                fresh.close();
            }
            assertThrows(ExistentialException.class, () -> view.size());
        }
    }

    @Test
    void readAndLockRequiresBothReadAndLockPermissionsDespiteGeneralGrant()
    {
        try (Existential ex = new Existential())
        {
            // @formatter:off
            ex.configure().context("/city").intent(Cat.class).on(cat -> true, "General access grant");
            ex.configure().context(OP).intent(Cat.class)
                    .read(cat -> "Black".equals(cat.color), "Only black cats can be read")
                    .rl(cat -> "Garden".equals(cat.location()), "Only garden cats can be locked");
            // @formatter:on
            Tr tr = ex.begin(OP);
            try
            {
                assertThrows(IntentViolation.class,
                        () -> Report.READ_LOCK.send(ex, tr, new Cat("Grey", "Garden"), false));
                assertThrows(IntentViolation.class,
                        () -> Report.READ_LOCK.send(ex, tr, new Cat("Black", "Park"), true));
                assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
                Report.READ_LOCK.send(ex, tr, new Cat("Black", "Garden"), false);
                tr.commit();
            }
            finally
            {
                tr.close();
            }
        }
    }

    enum Report
    {
        READ, READ_LOCK, WRITE, ACCESS;

        void send(Existential ex, Tr tr, Cat cat, boolean byId)
        {
            if (this == READ)
            {
                if (byId)
                {
                    ex.read(cat, tr.id());
                }
                else
                {
                    tr.read(cat);
                }
                return;
            }
            if (this == WRITE)
            {
                if (byId)
                {
                    ex.write(cat, tr.id());
                }
                else
                {
                    tr.write(cat);
                }
                return;
            }
            Event<Cat> event = this == READ_LOCK ? new RL<>(cat) : new Access<>(cat);
            TypeKey<Cat> type = new TypeKey<>(Cat.class, ex.get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES));
            if (byId)
            {
                ex.event(event, cat, type, tr.id());
            }
            else
            {
                tr.event(event, cat, type);
            }
        }
    }
}
