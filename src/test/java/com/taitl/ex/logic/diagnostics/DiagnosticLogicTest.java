package com.taitl.ex.logic.diagnostics;

import com.taitl.ex.logic.diagnostics.actions.*;
import com.taitl.ex.logic.diagnostics.data.*;
import com.taitl.ex.logic.evaluation.events.actions.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.diagnostics.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;

class DiagnosticLogicTest
{
    DiagnosticSnapshot minimal()
    {
        return new DiagnosticSnapshot(DiagnosticCode.RULE_REJECTED, "Rule rejected", null, null, null, false,
                null, null, null, null, null, null, false, List.of(), List.of(), null, List.of());
    }

    @Test
    void memoizesAcrossThreads() throws Exception
    {
        AtomicInteger formats = new AtomicInteger();
        DiagnosticSnapshot diagnostic = new DiagnosticSnapshot(DiagnosticCode.RULE_REJECTED, "Rule rejected", null,
                null, null, false, null, null, null, null, null, null, false, List.of(), List.of(), null, List.of()) {
            public String message()
            {
                formats.incrementAndGet();
                return super.message();
            }
        };
        ExistentialException exception = new ExistentialException("fallback", null, diagnostic);
        assertEquals(0, formats.get());
        ExecutorService pool = Executors.newFixedThreadPool(4);
        try
        {
            List<Future<String>> results = new ArrayList<>();
            for (int i = 0; i < 10; i++)
                results.add(pool.submit(() -> exception.getMessage()));
            for (Future<String> result : results)
                assertEquals("error[RULE-REJECTED]: Rule rejected", result.get());
            assertEquals(1, formats.get());
        }
        finally
        {
            pool.shutdownNow();
        }
    }

    @Test
    void formattingFailureUsesFallbackOnce()
    {
        AtomicInteger formats = new AtomicInteger();
        DiagnosticSnapshot diagnostic = new DiagnosticSnapshot(DiagnosticCode.RULE_REJECTED, "Rule rejected", null,
                null, null, false, null, null, null, null, null, null, false, List.of(), List.of(), null, List.of()) {
            public String message()
            {
                formats.incrementAndGet();
                throw new IllegalArgumentException("formatting failed");
            }
        };
        ExistentialException exception = new ExistentialException(null, null, diagnostic);
        assertNull(exception.getMessage());
        assertNull(exception.getMessage());
        assertEquals(1, formats.get());
    }

    @Test
    void constructionFailureKeepsOriginal()
    {
        DiagnosticLogic logic = new DiagnosticLogic();
        logic.build = new BuildDiagnostic() {
            public Diagnostic call(DiagnosticCode code, String operation, RuleOrigin origin, RuntimeKey<?> key,
                    StageName stage, String boundary, boolean stopped)
            {
                throw new IllegalStateException("construction failed");
            }
        };
        RuntimeException original = new RuntimeException("business failure");
        assertSame(original, logic.failure(original, null, null, null, null, null, false, true));
    }

    @Test
    void classifiesByCodeBeforeText()
    {
        Diagnostic callback = new DiagnosticSnapshot(DiagnosticCode.CALLBACK_FAILED, "condition not met", null,
                null, null, false, null, null, null, null, null, null, true, List.of(), List.of(), null, List.of());
        assertFalse(OnException.constraintViolation(new ConditionNotMetException("condition not met", null, callback)));
        assertTrue(OnException.constraintViolation(new EventHandlerException("unrelated", null, minimal())));
    }

    @Test
    void copiesListsAndPreservesDuplicates()
    {
        List<String> notes = new ArrayList<>(List.of("same", "same"));
        List<Diagnostic> children = new ArrayList<>(List.of(minimal(), minimal()));
        Diagnostic diagnostic = new DiagnosticSnapshot(DiagnosticCode.EVALUATION_FAILED, "Failed", null,
                null, null, false, null, null, null, null, null, null, false, notes, notes, null, children);
        notes.clear();
        children.clear();
        assertEquals(List.of("same", "same"), diagnostic.notes());
        assertEquals(List.of("same", "same"), diagnostic.actions());
        assertEquals(2, diagnostic.children().size());
    }

    @Test
    void copiesLibraryCausesStacksAndSuppressedFailures()
    {
        RuntimeException cause = new IllegalArgumentException("cause");
        EventHandlerException original = NewEvaluationFailure.handler("failed", cause);
        Throwable suppressed = new IllegalStateException("suppressed");
        original.addSuppressed(suppressed);
        ExistentialException copy = new DiagnoseFailure().call(original, minimal());
        assertEquals(EventHandlerException.class, copy.getClass());
        assertSame(cause, copy.getCause());
        assertArrayEquals(original.getStackTrace(), copy.getStackTrace());
        assertArrayEquals(new Throwable[] { suppressed }, copy.getSuppressed());
    }

    @Test
    void boundaryCopyKeepsFallbackWithoutRendering()
    {
        DiagnosticSnapshot broken = new DiagnosticSnapshot(DiagnosticCode.CALLBACK_FAILED, "Callback failed", null,
                null, null, false, null, null, null, null, null, null, true, List.of(), List.of(), null, List.of()) {
            public String message()
            {
                throw new IllegalStateException("formatting failed");
            }
        };
        EventHandlerException failure = new EventHandlerException("Original detail", null,
                new FailureSnapshot(minimal(), "Original detail"));
        ExistentialException copy = new DiagnoseFailure().boundary(failure, broken);
        assertEquals("Original detail", copy.getMessage());
    }

    @Test
    void retainsCallerSubclass()
    {
        class CustomViolation extends InvariantViolation
        {
        }
        CustomViolation original = new CustomViolation();
        ExistentialException copy = new DiagnoseFailure().call(original, minimal());
        assertEquals(InvariantViolation.class, copy.getClass());
        assertSame(original, copy.getCause());
        assertNull(original.diagnostic());
    }
}
