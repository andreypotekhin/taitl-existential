package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.diagnostics.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;

/** Acceptance for EventsAndConstraints.spec.md#structured-violation-diagnostics and #diagnostic-access-and-compatibility. */
class UserCanDiagnoseRuleViolations
{
    static final String OP = "/city/cats/save";

    Existential library()
    {
        Existential ex = new Existential();
        ex.off(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
        return ex;
    }

    @Test
    void stableCodes()
    {
        assertEquals("RULE-REJECTED", DiagnosticCode.RULE_REJECTED.id());
        assertEquals("INTENT-REJECTED", DiagnosticCode.INTENT_REJECTED.id());
        assertEquals("INTENT-MISSING", DiagnosticCode.INTENT_MISSING.id());
        assertEquals("CALLBACK-FAILED", DiagnosticCode.CALLBACK_FAILED.id());
        assertEquals("EVALUATION-FAILED", DiagnosticCode.EVALUATION_FAILED.id());
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void combinedSelectionsRetainOccurrences(boolean full)
    {
        try (Existential ex = library())
        {
            if (full)
                ex.on(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
            ex.configure()
                    .context("/city")
                    .invariant(new TypeKey<Being>(Being.class, full))
                    .all(entity -> false, "Being expression");
            ex.configure()
                    .context(OP)
                    .invariant(new Invariant<>(new TypeKey<Cat>(Cat.class, full))
                            .read(cat -> false, "Read check")
                            .on(cat -> false, "General check"));
            Tr tr = ex.begin(OP);
            try
            {
                tr.read(new Cat("Black", "Park"));
                tr.read(new Cat("Grey", "Garden"));
                ValidationStageExceptions failure = assertThrows(ValidationStageExceptions.class, () -> tr.commit());
                List<Diagnostic> children = failure.diagnostic().children();
                assertEquals(6, children.size());
                assertEquals(
                        List.of("Being expression", "Read check", "General check", "Being expression", "Read check",
                                "General check"),
                        descriptions(children));
                assertEquals("/city", children.get(0).context());
                assertEquals(full ? "com.taitl.ex.examples.night_city.model.Being" : "Being",
                        children.get(0).ruleType());
                assertEquals(full ? "com.taitl.ex.examples.night_city.model.Cat" : "Cat", children.get(0).type());
            }
            finally
            {
                tr.close();
            }
        }
    }

    @Test
    void localIntentOccurrencesAreNotDeduplicatedByHandler()
    {
        AtomicInteger calls = new AtomicInteger();
        On<Cat> reused = new On<>(cat -> calls.incrementAndGet() < 3, null, "Same handler");
        try (Existential ex = library())
        {
            ex.configure().context(OP).intent(new Intent<>(Cat.class).add(reused), new TypeKey<>(Cat.class));
            Transaction local = new Transaction(OP, "Local");
            local.intent(new Intent<>(Cat.class).add(reused).add(reused));
            Tr tr = ex.begin(OP, local);
            try
            {
                IntentViolation failure = assertThrows(IntentViolation.class, () -> tr.read(new Cat("Black", "Park")));
                assertEquals(3, calls.get());
                assertTrue(failure.diagnostic().transactionLocal());
                assertNull(failure.diagnostic().context());
                assertEquals("Same handler", failure.diagnostic().rule());
            }
            finally
            {
                tr.close();
            }
        }
    }

    @Test
    void boundaryDecorationDoesNotRender() throws Exception
    {
        RuntimeException original = new IllegalStateException("Original detail");
        try (Existential ex = library())
        {
            ex.configure().context(OP).invariant(Cat.class).read(cat -> {
                throw original;
            }, "Callback");
            Tr tr = ex.begin(OP);
            try
            {
                tr.read(new Cat("Black", "Park"));
                EventHandlerException failure = assertThrows(EventHandlerException.class, () -> tr.commit());
                Field rendered = ExistentialException.class.getDeclaredField("renderedReady");
                rendered.setAccessible(true);
                assertFalse(rendered.getBoolean(failure));
                assertSame(original, failure.getCause());
                assertEquals(
                        List.of("Evaluation stopped; later work was not evaluated", "Transaction is rollback-only"),
                        failure.diagnostic().notes());
            }
            finally
            {
                tr.close();
            }
        }
    }

    @Test
    void rollbackCallbacksContinueAfterFailure()
    {
        AtomicInteger calls = new AtomicInteger();
        RuntimeException original = new IllegalStateException("Rollback callback failure");
        try (Existential ex = library())
        {
            ex.configure().context(OP).invariant(Cat.class).read(cat -> true);
            Transaction local = new Transaction(OP, "Rollback callbacks");
            local.rollback(transaction -> {
                throw original;
            });
            local.rollback(transaction -> calls.incrementAndGet());
            Tr tr = ex.begin(OP, local);
            try
            {
                EventHandlerException failure = assertThrows(EventHandlerException.class, () -> tr.rollback());
                assertEquals(1, calls.get());
                assertEquals(StageName.ROLLBACK, failure.diagnostic().stage());
                assertEquals("rollback", failure.diagnostic().boundary());
                assertFalse(failure.diagnostic().evaluationStopped());
                assertTrue(failure.diagnostic().notes().isEmpty());
                assertSame(original, failure.getCause());
            }
            finally
            {
                tr.close();
            }
        }
    }

    @Test
    void nestedReportingRetainsItsOwnBoundary()
    {
        AtomicReference<Tr> active = new AtomicReference<>();
        try (Existential ex = library())
        {
            ex.configure().context(OP).immediate().invariant(Cat.class).read(cat -> false, "Immediate rejection");
            Transaction local = new Transaction(OP, "Reporting callback");
            local.checkpoint(transaction -> active.get().read(new Cat("Black", "Park")));
            Tr tr = ex.begin(OP, local);
            active.set(tr);
            try
            {
                EventHandlerException failure = assertThrows(EventHandlerException.class, () -> tr.checkpoint());
                ExistentialExceptions report = assertInstanceOf(ExistentialExceptions.class, failure.getCause());
                assertEquals("checkpoint", failure.diagnostic().boundary());
                assertEquals("report", report.diagnostic().boundary());
                assertEquals("report", report.diagnostic().children().get(0).boundary());
                assertEquals(StageName.IMMEDIATE, report.diagnostic().children().get(0).stage());
            }
            finally
            {
                tr.close();
            }
        }
    }

    @Nested
    @DisplayName("A caller can identify each declaration and its invocation without parsing messages")
    class Provenance
    {
        @ParameterizedTest
        @ValueSource(booleans = { false, true })
        void origins(boolean checkpoint)
        {
            try (Existential ex = library())
            {
                On<Cat> reused = new On<>(cat -> false, null, "CAT-01");
                ex.configure().context("/city").invariant(new Invariant<>(Cat.class).add(reused));
                ex.configure().context("/city/*/save").invariant(new Invariant<>(Cat.class).add(reused));
                Transaction local = new Transaction(OP, "Local");
                local.invariant(new Invariant<>(Cat.class).on(cat -> false, "Local rule"));
                Tr tr = ex.begin(OP, local);
                try
                {
                    tr.read(new Cat("Black", "Park"));
                    ValidationStageExceptions failure = assertThrows(ValidationStageExceptions.class, () -> {
                        if (checkpoint)
                            tr.checkpoint();
                        else
                            tr.commit();
                    });
                    assertEquals(3, failure.multiple().length);
                    List<Diagnostic> children = failure.diagnostic().children();
                    assertEquals(List.of("CAT-01", "CAT-01", "Local rule"),
                            UserCanDiagnoseRuleViolations.this.descriptions(children));
                    assertEquals("/city", children.get(0).context());
                    assertEquals("/city/*/save", children.get(1).context());
                    assertNull(children.get(2).context());
                    assertTrue(children.get(2).transactionLocal());
                    for (int i = 0; i < children.size(); i++)
                    {
                        Diagnostic d = children.get(i);
                        assertSame(failure.multiple()[i].diagnostic(), d);
                        assertEquals(OP, d.operation());
                        assertEquals("Read", d.event());
                        assertEquals("Cat", d.type());
                        assertEquals("Event", d.ruleEvent());
                        assertEquals(StageName.VALIDATION, d.stage());
                        assertEquals(checkpoint ? "checkpoint" : "commit", d.boundary());
                        assertFalse(d.evaluationStopped());
                        assertTrue(d.notes().isEmpty());
                    }
                    assertEquals(List.of("Transaction is rollback-only"), failure.diagnostic().notes());
                    assertFalse(failure.diagnostic().evaluationStopped());
                    assertEquals(1, occurrences(failure.getMessage(), "operation: "));
                    assertEquals(1, occurrences(failure.getMessage(), "evaluation: "));
                    assertEquals(1, occurrences(failure.getMessage(), "rollback-only"));
                    assertSame(failure.multiple()[0], failure.getCause());
                    assertArrayEquals(Arrays.copyOfRange(failure.multiple(), 1, 3), failure.getSuppressed());
                    ExistentialException[] copy = failure.multiple();
                    copy[0] = null;
                    assertNotNull(failure.multiple()[0]);
                    assertThrows(ExistentialException.class, () -> tr.read(new Cat("Black", "Park")));
                    assertThrows(NotFoundException.class, () -> ex.transactions().tr(tr.id()));
                    tr.rollback();
                    assertEquals(3, failure.diagnostic().children().size());
                }
                finally
                {
                    tr.close();
                }
            }
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "Amount must be positive", "CAT-01", "  keep spaces  ",
                "line\r\n\t\u0001\u2028injected" })
        void descriptions(String description)
        {
            try (Existential ex = library())
            {
                ex.configure().context(OP).invariant(new Invariant<>(Cat.class).on(cat -> false, description));
                Tr tr = ex.begin(OP);
                try
                {
                    tr.create(new Cat("Black", "Park"));
                    ValidationStageExceptions failure =
                            assertThrows(ValidationStageExceptions.class, () -> tr.commit());
                    Diagnostic d = failure.diagnostic().children().get(0);
                    assertEquals(description == null || description.isEmpty() ? null : description, d.rule());
                    String rendered = failure.multiple()[0].getMessage();
                    assertTrue(rendered.startsWith("error[RULE-REJECTED]: Rule rejected\n  operation: "));
                    if (d.rule() != null)
                    {
                        assertTrue(rendered.indexOf("rule:") < rendered.indexOf("context:"));
                        assertTrue(rendered.indexOf("context:") < rendered.indexOf("event:"));
                        assertTrue(rendered.indexOf("event:") < rendered.indexOf("evaluation:"));
                    }
                    else
                        assertFalse(rendered.contains("\n  rule:"));
                    assertFalse(rendered.contains("\r"));
                    assertFalse(rendered.contains("\t"));
                    assertFalse(rendered.contains("\u0001"));
                    if (description != null && description.contains("\n"))
                        assertTrue(rendered.contains("line\\r\\n\\t\\u0001\\u2028injected"));
                }
                finally
                {
                    tr.close();
                }
            }
        }

        @ParameterizedTest
        @EnumSource(value = StageName.class, names = { "BEGIN", "IMMEDIATE" })
        void reportStages(StageName stage)
        {
            try (Existential ex = library())
            {
                if (stage == StageName.BEGIN)
                    ex.configure()
                            .context(OP)
                            .begin()
                            .invariant(new Invariant<>(Cat.class).on(cat -> false, "Report rule"));
                else
                    ex.configure()
                            .context(OP)
                            .immediate()
                            .invariant(new Invariant<>(Cat.class).on(cat -> false, "Report rule"));
                Tr tr = ex.begin(OP);
                try
                {
                    ExistentialExceptions failure = assertThrows(ExistentialExceptions.class,
                            () -> tr.read(new Cat("Black", "Park")));
                    Diagnostic d = failure.diagnostic().children().get(0);
                    assertEquals(stage, d.stage());
                    assertEquals("report", d.boundary());
                    assertTrue(failure.diagnostic().notes().isEmpty());
                    assertFalse(failure.getMessage().contains("rollback-only"));
                }
                finally
                {
                    tr.close();
                }
            }
        }

        @Test
        void inheritedGenericAndExpandedEvents()
        {
            try (Existential ex = library())
            {
                ex.configure().context(OP).invariant(new Invariant<>(new TypeKey<List<?>>() {
                })
                        .on(values -> false, "Numbers"));
                Tr tr = ex.begin(OP);
                try
                {
                    tr.event(new Create<>(new ArrayList<Integer>()), new ArrayList<Integer>(),
                            new TypeKey<ArrayList<Integer>>() {
                            });
                    Diagnostic d = assertThrows(ValidationStageExceptions.class, () -> tr.commit())
                            .diagnostic()
                            .children()
                            .get(0);
                    assertEquals("ArrayList<Integer>", d.type());
                    assertEquals("List<?>", d.ruleType());
                    assertEquals("Event", d.ruleEvent());
                }
                finally
                {
                    tr.close();
                }
            }
        }
    }

    @Nested
    @DisplayName("Authorization and callback crashes retain the actual failure")
    class Failures
    {
        @Test
        void narrowIntent()
        {
            try (Existential ex = library())
            {
                ex.configure().context("/city").intent(Cat.class).on(cat -> true, "Grant");
                ex.configure().context(OP).intent(Cat.class).read(cat -> false, "Narrow permission");
                Tr tr = ex.begin(OP);
                try
                {
                    IntentViolation failure =
                            assertThrows(IntentViolation.class, () -> tr.read(new Cat("Black", "Park")));
                    assertEquals(DiagnosticCode.INTENT_REJECTED, failure.diagnostic().code());
                    assertEquals("Narrow permission", failure.diagnostic().rule());
                    assertEquals(OP, failure.diagnostic().context());
                    assertInstanceOf(ConditionNotMetException.class, failure.getCause());
                    assertTrue(failure.getCause().getMessage().contains("Narrow permission"));
                    assertEquals("report", failure.diagnostic().boundary());
                    assertFalse(failure.getMessage().contains("rollback-only"));
                }
                finally
                {
                    tr.close();
                }
            }
        }

        @Test
        void missingIntent()
        {
            try (Existential ex = library())
            {
                ex.configure().context(OP).intent(Cat.class).read(cat -> true, "Cats");
                Tr tr = ex.begin(OP);
                try
                {
                    IntentViolation failure = assertThrows(IntentViolation.class, () -> tr.read(new QuietEntity()));
                    Diagnostic d = failure.diagnostic();
                    assertEquals(DiagnosticCode.INTENT_MISSING, d.code());
                    assertNull(d.rule());
                    assertNull(d.context());
                    assertFalse(d.transactionLocal());
                    assertEquals(List.of("Define a matching intent for this event/type or review intent gating"),
                            d.actions());
                }
                finally
                {
                    tr.close();
                }
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { "predicate", "action", "intent", "lifecycle" })
        void callback(String path)
        {
            RuntimeException original = new IllegalArgumentException("application failure");
            try (Existential ex = library())
            {
                Transaction local = new Transaction(OP, "Callbacks");
                if (path.equals("predicate"))
                    ex.configure().context(OP).invariant(new Invariant<>(Cat.class).on(cat -> {
                        throw original;
                    }, "Check"));
                if (path.equals("action"))
                    ex.configure().context(OP).effect(new Effect<>(Cat.class).on(cat -> {
                        throw original;
                    }, "Effect"));
                if (path.equals("intent"))
                    ex.configure().context(OP).intent(Cat.class).on(cat -> {
                        throw original;
                    }, "Permission");
                if (path.equals("lifecycle"))
                    ex.configure().context(OP).invariant(Cat.class).read(cat -> true);
                if (path.equals("lifecycle"))
                    local.begin(transaction -> {
                        throw original;
                    });
                Tr tr = null;
                try
                {
                    ExistentialException failure;
                    if (path.equals("lifecycle"))
                        failure = assertThrows(EventHandlerException.class, () -> ex.begin(OP, local));
                    else
                    {
                        tr = ex.begin(OP, local);
                        Tr active = tr;
                        if (path.equals("intent"))
                            failure = assertThrows(IntentViolation.class, () -> active.read(new Cat("Black", "Park")));
                        else
                        {
                            tr.read(new Cat("Black", "Park"));
                            failure = assertThrows(EventHandlerException.class, () -> active.commit());
                        }
                    }
                    assertEquals(DiagnosticCode.CALLBACK_FAILED, failure.diagnostic().code());
                    assertTrue(hasCause(failure, original));
                    assertTrue(failure.diagnostic().evaluationStopped());
                    if (path.equals("lifecycle"))
                    {
                        assertNull(failure.diagnostic().rule());
                        assertNull(failure.diagnostic().context());
                        assertEquals("begin", failure.diagnostic().boundary());
                    }
                }
                finally
                {
                    if (tr != null)
                        tr.close();
                }
            }
        }

        @Test
        void collectsChecksButStopsEffects()
        {
            for (boolean effects : new boolean[] { false, true })
            {
                try (Existential ex = library())
                {
                    AtomicInteger calls = new AtomicInteger();
                    AtomicInteger rollbacks = new AtomicInteger();
                    if (effects)
                        ex.configure()
                                .context(OP)
                                .effect(new Effect<>(Cat.class).on(cat -> calls.incrementAndGet(), "First effect"));
                    ex.configure()
                            .context(OP)
                            .invariant(new Invariant<>(Cat.class).on(cat -> false, "First failure")
                                    .on(cat -> false, "Second failure"));
                    Transaction local = new Transaction(OP, "Rollback");
                    local.rollback(transaction -> rollbacks.incrementAndGet());
                    Tr tr = ex.begin(OP, local);
                    try
                    {
                        tr.read(new Cat("Black", "Park"));
                        tr.read(new Cat("Grey", "Garden"));
                        ValidationStageExceptions failure =
                                assertThrows(ValidationStageExceptions.class, () -> tr.checkpoint());
                        assertEquals(effects ? 1 : 4, failure.multiple().length);
                        assertEquals(effects, failure.diagnostic().evaluationStopped());
                        assertEquals(effects ? 1 : 0, occurrences(failure.getMessage(), "Evaluation stopped"));
                        assertEquals(effects ? 1 : 0, calls.get());
                        assertEquals(0, rollbacks.get());
                        tr.rollback();
                        assertEquals(1, rollbacks.get());
                    }
                    finally
                    {
                        tr.close();
                    }
                }
            }
        }

        @Test
        void errorsPropagateUnchanged()
        {
            AssertionError original = new AssertionError("application error");
            try (Existential ex = library())
            {
                ex.configure().context(OP).invariant(new Invariant<>(Cat.class).on(cat -> {
                    throw original;
                }, "Crash"));
                Tr tr = ex.begin(OP);
                try
                {
                    tr.read(new Cat("Black", "Park"));
                    assertSame(original, assertThrows(AssertionError.class, () -> tr.commit()));
                }
                finally
                {
                    tr.close();
                }
            }
        }
    }

    @Nested
    @DisplayName("Diagnostics remain independent, immutable, and useful after disposal")
    class Ownership
    {
        @Test
        void detachedAndSerializable() throws Exception
        {
            ExistentialException retained;
            AtomicInteger calls = new AtomicInteger();
            try (Existential ex = library())
            {
                IndexDefinition<MultiIndex<String, QuietEntity>> index = IndexDefinition.multi(
                        "quiet", QuietEntity.class, entity -> "quiet");
                ex.configure().context(OP).index(index).invariant(new Invariant<>(QuietEntity.class).on(entity -> {
                    calls.incrementAndGet();
                    return true;
                }, "Shared check"));
                Object captured = new Object();
                Transaction local = new Transaction(OP, "Local captures");
                local.invariant(new Invariant<>(QuietEntity.class).on(entity -> captured == null, "Quiet local"));
                Tr tr = ex.begin(OP, local);
                MultiIndex<String, QuietEntity> view = tr.index(index);
                try
                {
                    tr.read(new QuietEntity());
                    retained = assertThrows(ValidationStageExceptions.class, () -> tr.commit());
                    tr.rollback();
                    assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
                    assertThrows(ExistentialException.class, () -> view.size());
                    Field holder = Tr.class.getDeclaredField("concrete");
                    holder.setAccessible(true);
                    Object concrete = holder.get(tr);
                    for (String name : List.of("transactions", "stageData", "tr", "tl", "el", "memoData"))
                    {
                        Field owned = concrete.getClass().getDeclaredField(name);
                        owned.setAccessible(true);
                        assertNull(owned.get(concrete), name);
                    }
                }
                finally
                {
                    tr.close();
                }
            }
            Diagnostic d = retained.diagnostic();
            assertEquals(1, calls.get());
            assertTrue(retained.getMessage().contains("Quiet"));
            assertSame(retained.getMessage(), retained.getMessage());
            assertEquals(1, calls.get());
            assertThrows(UnsupportedOperationException.class, () -> d.notes().add("mutable"));
            assertThrows(UnsupportedOperationException.class, () -> d.actions().add("mutable"));
            assertThrows(UnsupportedOperationException.class, () -> d.children().clear());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bytes))
            {
                out.writeObject(d);
            }
            Diagnostic copy;
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())))
            {
                copy = (Diagnostic) in.readObject();
            }
            assertEquals(d.children().get(0).rule(), copy.children().get(0).rule());
            for (Field field : d.getClass().getDeclaredFields())
            {
                if (Modifier.isStatic(field.getModifiers()))
                    continue;
                assertTrue(Modifier.isFinal(field.getModifiers()));
                Class<?> type = field.getType();
                assertTrue(type == String.class || type == boolean.class || type == StageName.class
                        || type == DiagnosticCode.class || type == List.class, field.toString());
            }
        }

        @Test
        void concurrentReuse() throws Exception
        {
            ConditionNotMetException supplied = new ConditionNotMetException("Caller-owned");
            try (Existential ex = library())
            {
                ex.configure().context("/city").invariant(new Invariant<>(Cat.class).on(cat -> {
                    throw supplied;
                }, "Shared declaration"));
                ExecutorService pool = Executors.newFixedThreadPool(2);
                try
                {
                    List<Future<ExistentialException>> futures = new ArrayList<>();
                    for (String operation : List.of("/city/one", "/city/two"))
                    {
                        futures.add(pool.submit(() -> {
                            Transaction local = new Transaction(operation, "Local");
                            local.invariant(new Invariant<>(Cat.class).on(cat -> false, operation));
                            Tr tr = ex.begin(operation, local);
                            try
                            {
                                tr.read(new Cat("Black", "Park"));
                                return assertThrows(ValidationStageExceptions.class, () -> tr.commit());
                            }
                            finally
                            {
                                tr.close();
                            }
                        }));
                    }
                    ExistentialException first = futures.get(0).get(20, TimeUnit.SECONDS);
                    ExistentialException second = futures.get(1).get(20, TimeUnit.SECONDS);
                    assertNull(supplied.diagnostic());
                    assertEquals("Caller-owned", supplied.getMessage());
                    assertNotSame(first.diagnostic(), second.diagnostic());
                    assertEquals("/city/one", first.diagnostic().children().get(1).rule());
                    assertEquals("/city/two", second.diagnostic().children().get(1).rule());
                    assertTrue(hasCause(first, supplied));
                    assertTrue(hasCause(second, supplied));
                }
                finally
                {
                    pool.shutdownNow();
                }
            }
        }

        @Test
        void oldConstructorsAndNullDescriptions()
        {
            RuntimeException cause = new RuntimeException("cause");
            ExistentialException plain = new ExistentialException("plain", cause);
            assertNull(plain.diagnostic());
            assertEquals("plain", plain.getMessage());
            assertSame(cause, plain.getCause());
            assertNull(new IntentViolation((String) null).getMessage());
            assertEquals("Event handler execution failed", new EventHandlerException((String) null).getMessage());
            assertNull(new ExistentialException(null, null).diagnostic());
            assertNotNull(new Invariant<>(Cat.class).on(cat -> true, (String) null));
            assertNotNull(new Invariant<>(Cat.class).all(cat -> true, (String) null));
        }
    }

    static class QuietEntity
    {
        public String toString()
        {
            throw new AssertionError("Entity must never be rendered");
        }
    }

    List<String> descriptions(List<Diagnostic> diagnostics)
    {
        List<String> descriptions = new ArrayList<>();
        for (Diagnostic diagnostic : diagnostics)
            descriptions.add(diagnostic.rule());
        return descriptions;
    }

    int occurrences(String text, String fragment)
    {
        return (text.length() - text.replace(fragment, "").length()) / fragment.length();
    }

    boolean hasCause(Throwable failure, Throwable original)
    {
        for (Throwable current = failure; current != null; current = current.getCause())
            if (current == original)
                return true;
        return false;
    }
}
