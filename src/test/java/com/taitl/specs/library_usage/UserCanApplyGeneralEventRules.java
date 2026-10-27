package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;

class UserCanApplyGeneralEventRules
{
    @ParameterizedTest(name = "full names={0}, elementary expansion disabled={1}")
    @CsvSource({ "false,false", "false,true", "true,false", "true,true" })
    @DisplayName("T10262601: general On constraints, effects and intents apply once to every built-in event report")
    void generalRulesAreNotSkippedT10262601(boolean full, boolean disabled)
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
            TypeKey<Cat> type = new TypeKey<>(Cat.class, full);
            AtomicBoolean allowed = new AtomicBoolean();
            List<String> trace = new ArrayList<>();
            List<Cat> subjects = new ArrayList<>();
            // @formatter:off
            ex.configure().context("/general")
                    .intent(new Intent<>(type).on(cat -> {
                        trace.add("intent");
                        return allowed.get();
                    }, "An explicit general grant is required"), type)
                    .invariant(new Invariant<>(type).on(cat -> {
                        trace.add("check");
                        subjects.add(cat);
                        return true;
                    }, "A general check sees the reported subject"))
                    .effect(new Effect<>(type).on(cat -> trace.add("effect"), "General effect"));
            // @formatter:on
            Cat cat = new Cat("Black", "Park");
            Cat after = new Cat("Black", "Garden");
            List<Event<Cat>> events = List.of(new Create<>(cat), new Update<>(cat), new Delete<>(cat),
                    new Read<>(cat), new RL<>(cat), new Write<>(cat), new Access<>(cat), new CU<>(cat),
                    new UD<>(cat), new CUD<>(cat), new Transit<>(cat, after), new Port<>(cat, after),
                    new Port<>(null, cat), new Port<>(cat, null));
            for (Event<Cat> event : events)
            {
                Tr tr = ex.begin("/general");
                try
                {
                    trace.clear();
                    subjects.clear();
                    allowed.set(false);
                    assertThrows(IntentViolation.class, () -> send(tr, event, cat, type));
                    assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
                    allowed.set(true);
                    send(tr, event, cat, type);
                    send(tr, event, cat, type);
                    tr.commit();
                    assertEquals(List.of("intent", "intent", "intent", "check", "effect"), trace,
                            event.getClass().getSimpleName());
                    Cat expected = cat;
                    if (event instanceof BiEvent<?>)
                    {
                        BiEvent<Cat> change = (BiEvent<Cat>) event;
                        expected = change.t1 != null ? change.t1 : change.t0;
                    }
                    assertEquals(1, subjects.size());
                    assertSame(expected, subjects.get(0));
                }
                finally
                {
                    tr.close();
                }
            }
        }
    }

    void send(Tr tr, Event<Cat> event, Cat cat, TypeKey<Cat> type)
    {
        if (event instanceof BiEvent<?>)
        {
            tr.event((BiEvent<Cat>) event, type);
        }
        else
        {
            tr.event(event, cat, type);
        }
    }

    @Test
    void generalAndSpecificIntentsPreserveContextAndLocalDeclarationOrder()
    {
        try (Existential ex = new Existential())
        {
            List<String> trace = new ArrayList<>();
            // @formatter:off
            ex.configure().context("/general/child").intent(Cat.class)
                    .rl(cat -> trace.add("child-lock"))
                    .on(cat -> trace.add("child-general"));
            ex.configure().context("/general").intent(Cat.class)
                    .on(cat -> trace.add("parent-general"))
                    .read(cat -> trace.add("parent-read"));
            // @formatter:on
            Transaction local = new Transaction("/general/child", "Local ordered permissions");
            local.intent(new Intent<>(Cat.class)
                    .read(cat -> trace.add("local-read"))
                    .on(cat -> trace.add("local-general"))
                    .rl(cat -> trace.add("local-lock")));
            Tr tr = ex.begin("/general/child", local);
            try
            {
                Cat cat = new Cat("Black", "Park");
                tr.event(new RL<>(cat), cat, new TypeKey<>(Cat.class));
                assertEquals(List.of("parent-general", "parent-read", "child-lock", "child-general", "local-read",
                        "local-general", "local-lock"), trace);
                tr.commit();
            }
            finally
            {
                tr.close();
            }
        }
    }

    @ParameterizedTest
    @EnumSource(UserCanValidateAccessEvents.Report.class)
    void generalEffectsKeepValidationOrderAndStopAfterFirstFailure(UserCanValidateAccessEvents.Report report)
    {
        try (Existential ex = new Existential())
        {
            List<String> trace = new ArrayList<>();
            // @formatter:off
            ex.configure().context("/ordered-access")
                    .effect(new Effect<>(Cat.class).on(cat -> trace.add("before:" + cat.color)))
                    .invariant(new Invariant<>(Cat.class).on(cat -> {
                        trace.add("check:" + cat.color);
                        return "Black".equals(cat.color);
                    }, "Black cats pass"))
                    .effect(new Effect<>(Cat.class).on(cat -> trace.add("after:" + cat.color)));
            // @formatter:on
            Tr tr = ex.begin("/ordered-access");
            try
            {
                report.send(ex, tr, new Cat("Black", "Park"), false);
                report.send(ex, tr, new Cat("Grey", "Park"), false);
                report.send(ex, tr, new Cat("White", "Park"), false);
                assertThrows(ValidationStageExceptions.class, () -> tr.commit());
                assertEquals(List.of("before:Black", "check:Black", "after:Black", "before:Grey", "check:Grey"), trace);
                tr.rollback();
            }
            finally
            {
                tr.close();
            }
        }
    }
}
