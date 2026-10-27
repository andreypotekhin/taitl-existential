package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.logic.library.*;
import com.taitl.ex.logic.diagnostics.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.diagnostics.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.io.*;
import java.nio.charset.*;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

/** LibraryAndConfiguration.spec.md#operational-error-metadata. */
class UserCanDiagnoseOperationalFailures
{
    static final String OP = "/city/operational-errors";
    Existential ex = new Existential();

    @AfterEach
    void close()
    {
        Thread.interrupted();
        ex.close();
    }

    void configure()
    {
        ex.configure().context(OP).invariant(Cat.class).read(cat -> true, "Valid configuration");
    }

    @ParameterizedTest
    @ValueSource(strings = { "unknown.key=true", "behavior.rules.requireDescriptions=invalid",
            "shutdown.timeoutMillis=-1" })
    void invalidPropertiesKeepJavaCatchTypeAndAllowCorrectedRetry(String content)
    {
        ClassLoader resources = new ClassLoader() {
            public InputStream getResourceAsStream(String resource)
            {
                return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            }
        };
        ConfigureLibrary loader = new ConfigureLibrary(ex, name -> null, resources);
        IllegalStateException failure =
                assertThrows(IllegalStateException.class, () -> loader.fromClasspath("invalid"));
        assertEquals(IllegalStateException.class, failure.getClass());
        assertCode(failure, DiagnosticCode.CONFIGURATION_INVALID);
        configure();
        complete();
    }

    @Test
    void missingSourcePreservesOriginalIoCause()
    {
        ConfigureLibrary loader = new ConfigureLibrary(ex, name -> null, new ClassLoader(null) {
        });
        IllegalStateException failure =
                assertThrows(IllegalStateException.class, () -> loader.fromClasspath("missing"));
        assertCode(failure, DiagnosticCode.CONFIGURATION_INVALID);
        assertInstanceOf(FileNotFoundException.class, failure.getCause());
    }

    @Test
    void unconfiguredAndFinalizedGuardsHaveDifferentCategories()
    {
        assertCode(assertThrows(IllegalStateException.class, () -> ex.begin(OP)), DiagnosticCode.CONFIGURATION_INVALID);
        assertEquals(0, ex.transactions().logic().registry().size());
        configure();
        complete();
        assertCode(assertThrows(IllegalStateException.class, () -> ex.configure()),
                DiagnosticCode.CONFIGURATION_FROZEN);
    }

    @Test
    void timeoutAndAdmissionPreserveDrainingAndExplicitCleanup()
    {
        configure();
        ex.shutdownPolicy(ShutdownPolicy.graceful(Duration.ofMillis(1)));
        Tr tr = ex.begin(OP);
        try
        {
            IllegalStateException timeout = assertThrows(IllegalStateException.class, () -> ex.close());
            assertCode(timeout, DiagnosticCode.SHUTDOWN_TIMEOUT);
            assertTrue(timeout.getMessage().contains(tr.id()));
            assertCode(assertThrows(IllegalStateException.class, () -> ex.begin(OP)), DiagnosticCode.LIBRARY_CLOSED);
            tr.rollback();
            ex.close();
            assertCode(assertThrows(IllegalStateException.class, () -> ex.begin(OP)), DiagnosticCode.LIBRARY_CLOSED);
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void interruptionRetainsStatusCauseAndRetry()
    {
        configure();
        Tr tr = ex.begin(OP);
        try
        {
            Thread.currentThread().interrupt();
            IllegalStateException interrupted = assertThrows(IllegalStateException.class, () -> ex.close());
            assertCode(interrupted, DiagnosticCode.SHUTDOWN_INTERRUPTED);
            assertTrue(Thread.currentThread().isInterrupted());
            assertInstanceOf(InterruptedException.class, interrupted.getCause());
        }
        finally
        {
            Thread.interrupted();
            tr.close();
        }
        ex.close();
    }

    @Test
    void callbackCloseKeepsCallbackClassificationAndDiagnosesTheUnderlyingGuard()
    {
        configure();
        Transaction local = new Transaction(OP, "Do not close the library from a callback");
        local.commit(transaction -> ex.close());
        Tr tr = ex.begin(OP, local);
        try
        {
            EventHandlerException failure = assertThrows(EventHandlerException.class, () -> tr.commit());
            assertEquals(DiagnosticCode.CALLBACK_FAILED, failure.diagnostic().code());
            assertCode(assertInstanceOf(IllegalStateException.class, failure.getCause()),
                    DiagnosticCode.REENTRANT_LIFECYCLE);
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void memoAdmissionDoesNotOverwriteAcceptedStateAndCanBeCorrected()
    {
        configure();
        Tr tr = ex.begin(OP);
        Cat live = new Cat("Black", "Park");
        Cat before = new Cat("Black", "Park");
        try
        {
            MemoException invalid = assertThrows(MemoException.class, () -> tr.memo(live, live, Cat.class));
            assertEquals(MemoException.class, invalid.getClass());
            assertEquals(DiagnosticCode.MEMO_INVALID, invalid.diagnostic().code());
            tr.memo(before, live, Cat.class);
            MemoException duplicate = assertThrows(MemoException.class, () -> tr.memo(before, live, Cat.class));
            assertEquals(DiagnosticCode.MEMO_INVALID, duplicate.diagnostic().code());
            assertSame(before, tr.beforeState(live, new com.taitl.existential.keys.TypeKey<>(Cat.class)));
            tr.read(live);
            tr.commit();
            assertNotNull(invalid.getMessage());
        }
        finally
        {
            tr.close();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void missingMemoRetainsItsStockTypeAtReportAndCommit(boolean boundary)
    {
        com.taitl.existential.builders.ContextBuilder context = ex.configure().context(OP);
        if (!boundary)
            context.immediate();
        context.effect(Cat.class).transit((before, after) -> {
        }, "Requires before-state");
        Tr tr = ex.begin(OP);
        try
        {
            MemoException failure = assertThrows(MemoException.class, () -> {
                tr.update(new Cat("Black", "Park"));
                if (boundary)
                    tr.commit();
            });
            assertEquals(MemoException.class, failure.getClass());
            assertEquals(DiagnosticCode.MEMO_MISSING, failure.diagnostic().code());
            assertNull(failure.getCause());
            tr.rollback();
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void maintenanceFailureRetainsExtractorCauseAfterCleanup() throws Exception
    {
        RuntimeException cause = new IllegalArgumentException("extractor cause");
        IndexDefinition<MultiIndex<String, Cat>> broken = IndexDefinition.multi("broken", Cat.class, cat -> {
            throw cause;
        });
        ex.configure().context(OP).index(broken);
        Tr tr = ex.begin(OP);
        Diagnostic retained;
        try
        {
            tr.read(new Cat("Black", "Park"));
            ExistentialException failure = assertThrows(ExistentialException.class, () -> tr.index(broken).size());
            assertEquals(ExistentialException.class, failure.getClass());
            Throwable root = failure;
            while (root.getCause() != null)
                root = root.getCause();
            assertSame(cause, root);
            retained = failure.diagnostic();
            assertEquals(DiagnosticCode.INDEX_MAINTENANCE_FAILED, retained.code());
            assertEquals(OP, retained.operation());
            tr.rollback();
        }
        finally
        {
            tr.close();
        }
        ex.close();
        assertTrue(retained.notes().get(0).contains("broken"));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes))
        {
            out.writeObject(retained);
        }
        assertTrue(bytes.size() > 0);
    }

    @Test
    void closedMemoQueryKeepsJavaCatchTypeAndRequiresOnlyAFreshTransaction()
    {
        configure();
        Tr tr = ex.begin(OP);
        tr.close();
        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> tr.hasMemo(new Cat("Black", "Park"), new com.taitl.existential.keys.TypeKey<>(Cat.class)));
        assertCode(failure, DiagnosticCode.TRANSACTION_CLOSED);
        assertTrue(failure.getMessage().contains("Begin a new transaction for additional work"));
        complete();
    }

    @Test
    void unsupportedDecorationFallsBackAndControlCharactersCannotInjectLines()
    {
        assertEquals("original", OperationalDiagnostics.message(DiagnosticCode.TRANSACTION_ROLLBACK_ONLY, "original"));
        String detail = "bad\r\n\t\u001bvalue";
        String rendered = OperationalDiagnostics.message(DiagnosticCode.CONFIGURATION_INVALID, detail);
        assertTrue(rendered.contains("bad\\r\\n\\t\\u001bvalue"));
        assertFalse(rendered.contains("\r"));
        assertNull(new MemoException("caller").diagnostic());
    }

    @Test
    void standaloneLibraryFailuresAndCleanupKeepCodesAndOriginalCauses()
    {
        com.taitl.existential.handlers.On<Cat> check = new com.taitl.existential.handlers.On<>(cat -> false, null,
                "Standalone condition");
        ConditionNotMetException rejected = assertThrows(ConditionNotMetException.class,
                () -> check.handle(new Cat("Black", "Park")));
        assertEquals(DiagnosticCode.RULE_REJECTED, rejected.diagnostic().code());
        assertTrue(rejected.getMessage().contains("Standalone condition"));
        RuntimeException original = new IllegalArgumentException("Original callback");
        com.taitl.existential.handlers.On<Cat> handler = new com.taitl.existential.handlers.On<>(cat -> {
            throw original;
        },
                "Standalone callback");
        EventHandlerException callback = assertThrows(EventHandlerException.class,
                () -> handler.handle(new Cat("Black", "Park")));
        assertEquals(DiagnosticCode.CALLBACK_FAILED, callback.diagnostic().code());
        assertSame(original, callback.getCause());
        original.addSuppressed(new IOException("Other cleanup"));
        ExistentialException cleanup = OperationalDiagnostics.failure(DiagnosticCode.CLEANUP_FAILED,
                "Cleanup failed", null, original);
        assertSame(original, cleanup.getCause());
        assertEquals(1, cleanup.getCause().getSuppressed().length);
        assertEquals(DiagnosticCode.CLEANUP_FAILED, cleanup.diagnostic().code());
    }

    void assertCode(Throwable failure, DiagnosticCode code)
    {
        assertEquals(IllegalStateException.class, failure.getClass());
        assertTrue(failure.getMessage().contains("error[" + code.id() + "]:"), failure.getMessage());
        assertTrue(failure.getMessage().contains("#diagnostic-" + code.id().toLowerCase(java.util.Locale.ROOT)));
    }

    void complete()
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
