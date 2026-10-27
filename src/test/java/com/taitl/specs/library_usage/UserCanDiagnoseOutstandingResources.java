package com.taitl.specs.library_usage;

import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class UserCanDiagnoseOutstandingResources
{
    @Test
    void shutdownFailureDescribesBoundedKnownOwnersAndSupportsCleanupRetry()
    {
        Existential ex = new Existential();
        List<Tr> active = new ArrayList<>();
        try
        {
            ex.configure().context("/known/operation").invariant(String.class).all(value -> true, "Allowed");
            ex.shutdownPolicy(ShutdownPolicy.graceful(Duration.ofMillis(1)));
            for (int i = 0; i < 12; i++)
            {
                active.add(ex.begin("/known/operation"));
            }
            IllegalStateException failure = assertThrows(IllegalStateException.class, () -> ex.close());
            String message = failure.getMessage();
            assertTrue(message.contains("activeTransactions=12"));
            assertTrue(message.contains("inFlight=0"));
            assertTrue(message.contains("/known/operation"));
            assertTrue(message.contains("retry close()"));
            assertEquals(8, message.split("@/known/operation", -1).length - 1);
            assertTrue(message.length() < 3000);
        }
        finally
        {
            active.forEach(tr -> tr.close());
            ex.close();
        }
    }

    @Test
    void interruptionReportsKnownOwnersWithoutCollectingGarbage()
    {
        Existential ex = new Existential();
        ex.configure().context("/interrupted").invariant(String.class).all(value -> true, "Allowed");
        Tr tr = ex.begin("/interrupted");
        try
        {
            Thread.currentThread().interrupt();
            IllegalStateException failure = assertThrows(IllegalStateException.class, () -> ex.close());
            assertTrue(failure.getMessage().contains(tr.id()));
            assertTrue(failure.getMessage().contains("activeTransactions=1"));
        }
        finally
        {
            Thread.interrupted();
            tr.close();
            ex.close();
        }
    }

    @Test
    void detachedBoundaryFailureIsNotRetainedForShutdownDiagnostics()
    {
        IndexDefinition<SingleIndex<Integer, String>> words =
                IndexDefinition.single("words", String.class, value -> value.length());
        Existential ex = new Existential();
        ex.configure().context("/detached").index(words).invariant(String.class).all(value -> false, "Reject");
        Tr tr = ex.begin("/detached");
        try
        {
            tr.create("value");
            assertThrows(ValidationStageExceptions.class, () -> tr.checkpoint());
            assertEquals(0, ex.transactions().logic().registry().size());
            assertDoesNotThrow(() -> ex.close());
        }
        finally
        {
            tr.close();
            ex.close();
        }
    }
}
