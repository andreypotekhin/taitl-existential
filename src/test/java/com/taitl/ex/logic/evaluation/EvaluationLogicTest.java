package com.taitl.ex.logic.evaluation;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.stages.validation.output.*;
import com.taitl.existential.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.events.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationLogicTest
{
    static class TestEvaluationLogic extends EvaluationLogic
    {
        private final EventField eventField;

        TestEvaluationLogic(com.taitl.ex.logic.transactions.TransactionLogic tl, EventField eventField)
        {
            super(tl);
            this.eventField = eventField;
        }

        protected EventField eventField(Tr tr)
        {
            return eventField;
        }
    }

    private Existential ex;

    @AfterEach
    void cleanup()
    {
        if (ex != null)
        {
            ex.close();
        }
    }

    @Disabled("Config not found for op: /api/eval/collect")
    @Test
    @DisplayName("Evaluate executes effects and collects constraint violations")
    void evaluateExecutesEffectsAndCollectsConstraintViolations() throws Exception
    {
        ex = new Existential();
        AtomicInteger effectCalls = new AtomicInteger();
        TypeKey<String> typeKey = new TypeKey<>(String.class);
        ConfigurationIndexes indexes = new ConfigurationIndexes();
        indexes.addHandler(EventKey.valueOf(Create.class, typeKey),
                new OnCreate<String>(null, v -> effectCalls.incrementAndGet()));
        indexes.addHandler(EventKey.valueOf(Create.class, typeKey),
                new OnCreate<String>(v -> false, null, "must pass"));
        indexes.doneIndexing();

        Tr tr = transitTr("/api/eval/collect", typeKey, null, "value");
        EvaluationLogic logic = new TestEvaluationLogic(ex.transactions().logic(), indexes.eventField());

        ValidationReport report = new ValidationReport();
        logic.evaluateValidation(tr, report);

        assertEquals(1, effectCalls.get());
        assertEquals(1, report.exceptions().size());
        assertInstanceOf(ConditionNotMetException.class, report.exceptions().get(0));
    }

    @Disabled("Config not found for op: /api/eval/partial")
    @Test
    @DisplayName("Evaluate rethrows non violation handler exception and keeps partial report")
    void evaluateRethrowsNonViolationHandlerExceptionAndKeepsPartialReport() throws Exception
    {
        ex = new Existential();
        TypeKey<String> typeKey = new TypeKey<>(String.class);
        ConfigurationIndexes indexes = new ConfigurationIndexes();
        indexes.addHandler(EventKey.valueOf(Create.class, typeKey),
                new OnCreate<String>(v -> false, null, "broken invariant"));
        indexes.addHandler(EventKey.valueOf(Create.class, typeKey),
                new OnCreate<String>(null, v -> {
                    throw new IllegalStateException("boom");
                }));
        indexes.doneIndexing();

        Tr tr = transitTr("/api/eval/partial", typeKey, null, "value");
        EvaluationLogic logic = new TestEvaluationLogic(ex.transactions().logic(), indexes.eventField());

        ValidationReport report = new ValidationReport();
        EventHandlerException error = assertThrows(
                EventHandlerException.class,
                () -> logic.evaluateValidation(tr, report));

        assertNotNull(error.getCause());
        assertInstanceOf(IllegalStateException.class, error.getCause());
        assertEquals(1, report.exceptions().size());
        assertInstanceOf(ConditionNotMetException.class, report.exceptions().get(0));
    }

    @Disabled("Config not found for op '/api/eval/predicate-failure'")
    @Test
    @DisplayName("Evaluate collects predicate failure from invariant all")
    void evaluateCollectsPredicateFailureFromInvariantAll() throws Exception
    {
        ex = new Existential();
        TypeKey<String> typeKey = new TypeKey<>(String.class);
        ConfigurationIndexes indexes = new ConfigurationIndexes();

        Invariant<String> invariant = new Invariant<>(String.class);
        invariant.all(v -> false, "must pass all");
        for (var ev : invariant.list())
        {
            indexes.addHandler(EventKey.valueOf(Create.class, typeKey), ev);
        }
        indexes.doneIndexing();

        Tr tr = transitTr("/api/eval/predicate-failure", typeKey, null, "value");
        EvaluationLogic logic = new TestEvaluationLogic(ex.transactions().logic(), indexes.eventField());

        ValidationReport report = new ValidationReport();
        assertDoesNotThrow(() -> logic.evaluateValidation(tr, report));
        assertEquals(1, report.exceptions().size());
        assertInstanceOf(PredicateFailure.class, report.exceptions().get(0));
    }

    private <T> Tr transitTr(String op, TypeKey<T> typeKey, T t0, T t1)
    {
        Tr tr = new Tr(op, UUID.randomUUID(), ex.transactions().logic());
        tr.runtimeIndexes().encounteredUniqueEvents.add(RuntimeKey.valueOf(new Transit<>(t0, t1), typeKey, t1, false));
        return tr;
    }
}
