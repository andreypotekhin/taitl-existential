package com.taitl.ex.logic.diagnostics;

import com.taitl.ex.logic.diagnostics.actions.*;
import com.taitl.existential.diagnostics.*;
import com.taitl.existential.exceptions.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionDiagnosticLogicTest
{
    @Test
    void constructionFailureRetainsLegacyFallbackAndStockType()
    {
        TransactionDiagnosticLogic logic = new TransactionDiagnosticLogic();
        logic.build = new BuildTransactionDiagnostic() {
            public Diagnostic call(DiagnosticCode code, String operation, String id, String action)
            {
                throw new IllegalStateException("formatting dependency failed");
            }
        };
        ExistentialException closed = logic.closed("/operation", "id", "commit");
        assertEquals(ExistentialException.class, closed.getClass());
        assertNull(closed.diagnostic());
        assertTrue(closed.getMessage().contains("Cannot commit because the transaction is closed"));
        assertNull(logic.rollbackOnly("/operation", "id", "commit").diagnostic());
        assertNull(logic.disposedView("/operation").diagnostic());
        assertTrue(logic.disposedView("/operation").getMessage().contains("Managed index view is disposed"));
        NotFoundException missing = logic.missing("id");
        assertEquals(NotFoundException.class, missing.getClass());
        assertNull(missing.diagnostic());
        assertTrue(missing.getMessage().contains("Transaction not found, id=id"));
    }

    @Test
    void errorsAreNotSwallowedDuringDecoration()
    {
        AssertionError original = new AssertionError("construction error");
        TransactionDiagnosticLogic logic = new TransactionDiagnosticLogic();
        logic.build = new BuildTransactionDiagnostic() {
            public Diagnostic call(DiagnosticCode code, String operation, String id, String action)
            {
                throw original;
            }
        };
        assertSame(original, assertThrows(AssertionError.class, () -> logic.closed("/operation", "id", "commit")));
    }

    @Test
    void builderOmitsUnknownFactsAndRejectsUnrelatedCodes()
    {
        BuildTransactionDiagnostic build = new BuildTransactionDiagnostic();
        Diagnostic d = build.call(DiagnosticCode.TRANSACTION_CLOSED, null, null, null);
        assertNull(d.operation());
        assertNull(d.boundary());
        assertTrue(d.notes().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> build.call(DiagnosticCode.RULE_REJECTED, null, null, null));
        assertEquals("TRANSACTION-CLOSED", d.code().id());
    }
}
