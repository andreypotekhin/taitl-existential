package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
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
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;

/** Acceptance for TransactionsAndEvaluation.spec.md#transaction-state-diagnostics. */
class UserCanDiagnoseTransactionState
{
    static final String OP = "/city/transaction-state";
    Existential ex;
    AtomicInteger checks;
    AtomicInteger rollbacks;
    IndexDefinition<MultiIndex<String, Cat>> cats;

    @BeforeEach
    void setup()
    {
        ex = new Existential();
        checks = new AtomicInteger();
        rollbacks = new AtomicInteger();
        cats = IndexDefinition.multi("cats", Cat.class, cat -> cat.location());
        ex.configure().context(OP).index(cats).invariant(Cat.class).read(cat -> {
            checks.incrementAndGet();
            return "Black".equals(cat.color);
        }, "Black cats pass");
    }

    @AfterEach
    void cleanup()
    {
        ex.close();
    }

    Tr begin()
    {
        Transaction local = new Transaction(OP, "Explicit cleanup");
        local.rollback(transaction -> rollbacks.incrementAndGet());
        return ex.begin(OP, local);
    }

    static List<Arguments> closedRequests()
    {
        List<Arguments> cases = new ArrayList<>();
        for (Ending ending : Ending.values())
            for (Request request : Request.values())
                cases.add(Arguments.of(ending, request));
        return cases;
    }

    static List<Arguments> failedRequests()
    {
        List<Arguments> cases = new ArrayList<>();
        for (boolean checkpoint : new boolean[] { false, true })
            for (Request request : Request.values())
                if (request != Request.ROLLBACK)
                    cases.add(Arguments.of(checkpoint, request));
        return cases;
    }

    @ParameterizedTest
    @MethodSource("closedRequests")
    void completedHandlesRejectFurtherWork(Ending ending, Request request)
    {
        Tr tr = begin();
        MultiIndex<String, Cat> view = tr.index(cats);
        try
        {
            ending.finish(tr);
            ExistentialException failure = assertThrows(ExistentialException.class, () -> request.invoke(tr, view));
            assertEquals(ExistentialException.class, failure.getClass());
            assertGuard(failure, DiagnosticCode.TRANSACTION_CLOSED, tr.id(), request);
            assertEquals(0, checks.get());
            assertEquals(ending == Ending.ROLLBACK ? 1 : 0, rollbacks.get());
            assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
            assertEquals(DiagnosticCode.TRANSACTION_NOT_FOUND,
                    assertThrows(NotFoundException.class, () -> ex.transactions().tr(tr.id())).diagnostic().code());
        }
        finally
        {
            tr.close();
        }
    }

    @ParameterizedTest
    @MethodSource("failedRequests")
    void rollbackOnlyHasItsOwnCodeAndStillAllowsExplicitCleanup(boolean checkpoint, Request request)
    {
        Tr tr = begin();
        MultiIndex<String, Cat> view = tr.index(cats);
        try
        {
            tr.read(new Cat("Grey", "Park"));
            assertThrows(ValidationStageExceptions.class, () -> {
                if (checkpoint)
                    tr.checkpoint();
                else
                    tr.commit();
            });
            assertEquals(1, checks.get());
            ExistentialException failure = assertThrows(ExistentialException.class, () -> request.invoke(tr, view));
            assertEquals(ExistentialException.class, failure.getClass());
            assertGuard(failure, DiagnosticCode.TRANSACTION_ROLLBACK_ONLY, tr.id(), request);
            assertNull(failure.getCause());
            assertEquals(1, checks.get());
            assertEquals(0, rollbacks.get());
            NotFoundException missing =
                    assertThrows(NotFoundException.class, () -> ex.read(new Cat("Black", "Park"), tr.id()));
            assertMissing(missing, tr.id());
            tr.rollback();
            assertEquals(1, rollbacks.get());
            assertEquals(DiagnosticCode.TRANSACTION_CLOSED,
                    assertThrows(ExistentialException.class, () -> tr.commit()).diagnostic().code());
            assertEquals(DiagnosticCode.TRANSACTION_ROLLBACK_ONLY, failure.diagnostic().code());
        }
        finally
        {
            tr.close();
        }
        Tr fresh = begin();
        try
        {
            fresh.read(new Cat("Black", "Garden"));
            fresh.commit();
            assertEquals(2, checks.get());
        }
        finally
        {
            fresh.close();
        }
    }

    @ParameterizedTest
    @EnumSource(Ending.class)
    void lookupDoesNotInventFormerOperation(Ending ending)
    {
        Tr tr = begin();
        try
        {
            ending.finish(tr);
            assertMissing(assertThrows(NotFoundException.class, () -> ex.transactions().tr(tr.id())), tr.id());
            assertMissing(assertThrows(NotFoundException.class, () -> ex.read(new Cat("Black", "Park"), tr.id())),
                    tr.id());
        }
        finally
        {
            tr.close();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "unknown", "id\r\n\t\u001b\u2028injected" })
    void missingIdsAreRawInFieldsAndEscapedInMessages(String id)
    {
        NotFoundException failure = assertThrows(NotFoundException.class, () -> ex.transactions().tr(id));
        assertMissing(failure, id);
        assertTrue(failure.diagnostic().notes().contains("Transaction ID: " + id));
        assertFalse(failure.getMessage().contains("\r"));
        assertFalse(failure.getMessage().contains("\t"));
        assertFalse(failure.getMessage().contains("\u001b"));
        assertSame(failure.getMessage(), failure.getMessage());
        if (id.contains("\n"))
            assertTrue(failure.getMessage().contains("id\\r\\n\\t\\u001b\\u2028injected"));
        assertEquals(0, checks.get());
    }

    @Test
    void failedIndexMaintenanceAlsoMakesTheHandleRollbackOnly()
    {
        IndexDefinition<MultiIndex<String, Cat>> broken = IndexDefinition.multi("broken", Cat.class, cat -> {
            throw new IllegalArgumentException("application extractor failure");
        });
        ex.configure().context(OP).index(broken);
        Tr tr = begin();
        try
        {
            tr.read(new Cat("Black", "Park"));
            assertThrows(RuntimeException.class, () -> tr.index(broken).size());
            assertGuard(assertThrows(ExistentialException.class, () -> tr.read(new Cat("Black", "Park"))),
                    DiagnosticCode.TRANSACTION_ROLLBACK_ONLY, tr.id(), Request.READ);
            tr.rollback();
            assertEquals(1, rollbacks.get());
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void callbackPropagationRetainsExistingCallbackClassification()
    {
        Tr closed = begin();
        closed.close();
        Transaction local = new Transaction(OP, "Callback invokes a closed handle");
        local.commit(transaction -> closed.read(new Cat("Black", "Park")));
        Tr tr = ex.begin(OP, local);
        try
        {
            EventHandlerException failure = assertThrows(EventHandlerException.class, () -> tr.commit());
            assertEquals(DiagnosticCode.CALLBACK_FAILED, failure.diagnostic().code());
            ExistentialException cause = assertInstanceOf(ExistentialException.class, failure.getCause());
            assertEquals(DiagnosticCode.TRANSACTION_CLOSED, cause.diagnostic().code());
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void snapshotsSurviveDisposalAndSerializationWithoutRenderingEntities() throws Exception
    {
        Tr tr = begin();
        tr.close();
        Cat hostile = new Cat("Black", "Park") {
            public String toString()
            {
                throw new AssertionError("must not render an entity");
            }
        };
        ExistentialException failure = assertThrows(ExistentialException.class, () -> tr.read(hostile));
        Diagnostic snapshot = failure.diagnostic();
        ex.close();
        assertEquals(OP, snapshot.operation());
        assertTrue(failure.getMessage().contains("TRANSACTION-CLOSED"));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.notes().clear());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.actions().clear());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.children().clear());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes))
        {
            out.writeObject(snapshot);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())))
        {
            Diagnostic restored = (Diagnostic) in.readObject();
            assertEquals(snapshot.code(), restored.code());
            assertEquals(snapshot.notes(), restored.notes());
            assertEquals(snapshot.operation(), restored.operation());
        }
        assertEquals(0, checks.get());
    }

    @Test
    void independentConcurrentHandlesKeepTheirOwnIds() throws Exception
    {
        Tr first = begin();
        Tr second = begin();
        first.close();
        second.close();
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try
        {
            List<Future<Diagnostic>> results = new ArrayList<>();
            for (Tr tr : List.of(first, second))
                results.add(
                        pool.submit(() -> assertThrows(ExistentialException.class, () -> tr.commit()).diagnostic()));
            Diagnostic one = results.get(0).get(10, TimeUnit.SECONDS);
            Diagnostic two = results.get(1).get(10, TimeUnit.SECONDS);
            assertNotSame(one, two);
            assertTrue(one.notes().contains("Transaction ID: " + first.id()));
            assertFalse(one.notes().contains("Transaction ID: " + second.id()));
            assertTrue(two.notes().contains("Transaction ID: " + second.id()));
        }
        finally
        {
            pool.shutdownNow();
        }
    }

    @Test
    void constructorCompatibility()
    {
        Throwable cause = new IllegalArgumentException("original cause");
        NotFoundException plain = new NotFoundException("plain", cause);
        assertEquals("plain", plain.getMessage());
        assertSame(cause, plain.getCause());
        assertNull(plain.diagnostic());
        assertEquals("Requested item was not found", new NotFoundException((String) null).getMessage());
        assertEquals("Requested item was not found", new NotFoundException(null, cause).getMessage());
        assertNull(new ExistentialException((String) null).diagnostic());
    }

    void assertGuard(ExistentialException failure, DiagnosticCode code, String id, Request request)
    {
        Diagnostic d = failure.diagnostic();
        assertEquals(code, d.code());
        assertEquals(OP, d.operation());
        assertEquals(request.boundary, d.boundary());
        List<String> notes = request == Request.INDEX && code == DiagnosticCode.TRANSACTION_CLOSED
                ? List.of("Requested action: " + request.action)
                : List.of("Requested action: " + request.action, "Transaction ID: " + id);
        assertEquals(notes, d.notes());
        assertAbsentEvaluation(d);
        assertEquals("/Troubleshooting.md#diagnostic-" + code.id().toLowerCase(Locale.ROOT), d.troubleshooting());
        assertFalse(d.actions().isEmpty());
        assertTrue(failure.getMessage().startsWith("error[" + code.id() + "]: "));
    }

    void assertMissing(NotFoundException failure, String id)
    {
        assertEquals(NotFoundException.class, failure.getClass());
        Diagnostic d = failure.diagnostic();
        assertEquals(DiagnosticCode.TRANSACTION_NOT_FOUND, d.code());
        assertNull(d.operation());
        assertNull(d.boundary());
        assertEquals(List.of("Transaction ID: " + id), d.notes());
        assertAbsentEvaluation(d);
        assertNull(failure.getCause());
    }

    void assertAbsentEvaluation(Diagnostic d)
    {
        assertNull(d.rule());
        assertNull(d.context());
        assertFalse(d.transactionLocal());
        assertNull(d.event());
        assertNull(d.type());
        assertNull(d.ruleEvent());
        assertNull(d.ruleType());
        assertNull(d.stage());
        assertTrue(d.children().isEmpty());
        assertFalse(d.evaluationStopped());
    }

    enum Ending
    {
        COMMIT, ROLLBACK, CLOSE;

        void finish(Tr tr)
        {
            switch (this)
            {
            case COMMIT:
                tr.commit();
                break;
            case ROLLBACK:
                tr.rollback();
                break;
            default:
                tr.close();
            }
        }
    }

    enum Request
    {
        READ("send events", "report"), CREATE("send events", "report"), COMMIT("commit", "commit"), CHECKPOINT(
                "checkpoint", "checkpoint"), ROLLBACK("rollback",
                        "rollback"), MEMO("register memo state", "report"), INDEX("access managed indexes", null);

        String action;
        String boundary;

        Request(String action, String boundary)
        {
            this.action = action;
            this.boundary = boundary;
        }

        void invoke(Tr tr, MultiIndex<String, Cat> view)
        {
            switch (this)
            {
            case READ:
                tr.read(new Cat("Black", "Park"));
                break;
            case CREATE:
                tr.create(new Cat("Black", "Park"));
                break;
            case COMMIT:
                tr.commit();
                break;
            case CHECKPOINT:
                tr.checkpoint();
                break;
            case ROLLBACK:
                tr.rollback();
                break;
            case MEMO:
                tr.memo(new Cat("Black", "Park"), new Cat("Black", "Park"), Cat.class);
                break;
            default:
                view.size();
            }
        }
    }
}
