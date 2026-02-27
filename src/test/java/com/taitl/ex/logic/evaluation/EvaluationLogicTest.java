package com.taitl.ex.logic.evaluation;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.validation.output.ValidationReport;
import com.taitl.existential.Existential;
import com.taitl.existential.events.*;
import com.taitl.existential.exceptions.ConditionNotMetException;
import com.taitl.existential.exceptions.EventHandlerException;
import com.taitl.existential.handlers.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.Tr;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
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
        logic.evaluate(tr, report);

        assertEquals(1, effectCalls.get());
        assertEquals(1, report.exceptions().size());
        assertInstanceOf(ConditionNotMetException.class, report.exceptions().get(0));
    }

    @Test
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
                () -> logic.evaluate(tr, report));

        assertNotNull(error.getCause());
        assertInstanceOf(IllegalStateException.class, error.getCause());
        assertEquals(1, report.exceptions().size());
        assertInstanceOf(ConditionNotMetException.class, report.exceptions().get(0));
    }

    private <T> Tr transitTr(String op, TypeKey<T> typeKey, T t0, T t1)
    {
        Tr tr = new Tr(op, UUID.randomUUID(), ex.transactions().logic());
        tr.runtimeIndexes().encounteredUniqueEvents.add(RuntimeKey.valueOf(new Transit<>(t0, t1), typeKey, t1, false));
        return tr;
    }
}
