package com.taitl.specs.library_usage;

import com.taitl.existential.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserCanQueryManagedIndexesDuringTransaction
{
    @Test
    void reportTimeRulesSeeAcceptedHistoryAndDeniedEventsAreNotEnrolled()
    {
        IndexDefinition<SingleIndex<Integer, String>> words =
                IndexDefinition.single("words", String.class, value -> value.length());
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            // @formatter:off
            ex.configure().context("/history").index(words)
                    .intent(String.class).create(value -> !value.startsWith("deny"), "Allowed value");
            ex.configure().context("/history").immediate().invariant(String.class)
                    .all(words, (value, index) -> index.get(value.length()) != value,
                            "The candidate is not accepted yet");
            // @formatter:on
            Tr tr = ex.begin("/history");
            assertThrows(IntentViolation.class, () -> tr.create("deny"));
            assertTrue(tr.index(words).isEmpty());
            tr.create("ok");
            assertEquals("ok", tr.index(words).get(2));
            tr.commit();
        }
    }

    @Test
    void extractorFailureDetachesHandleAndMakesEveryEscapedViewRollbackOnly()
    {
        IndexDefinition<SingleIndex<Integer, String>> words =
                IndexDefinition.single("words", String.class, value -> {
                    throw new IllegalArgumentException("bad key");
                });
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure().context("/failure").index(words);
            Tr tr = ex.begin("/failure");
            SingleIndex<Integer, String> view = tr.index(words);
            ExistentialException failure = assertThrows(ExistentialException.class, () -> tr.create("value"));
            assertTrue(failure.getMessage().contains("/failure"));
            assertThrows(NotFoundException.class, () -> ex.transactions().tr(tr.id()));
            assertThrows(ExistentialException.class, () -> view.size());
            assertThrows(ExistentialException.class, () -> tr.commit());
            tr.rollback();
        }
    }

    @Test
    void repeatedReportsDeferMultiIndexKeysUntilAccessAndFoldBetweenQueries()
    {
        java.util.concurrent.atomic.AtomicInteger extractions = new java.util.concurrent.atomic.AtomicInteger();
        IndexDefinition<MultiIndex<Integer, String>> words = IndexDefinition.multi("words", String.class, value -> {
            extractions.incrementAndGet();
            return value.length();
        });
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure().context("/folded").index(words);
            Tr tr = ex.begin("/folded");
            String value = new String("value");
            for (int i = 0; i < 10000; i++)
            {
                tr.create(value);
            }
            assertEquals(0, extractions.get());
            MultiIndex<Integer, String> view = tr.index(words);
            assertEquals(1, view.get(5).size());
            assertEquals(1, extractions.get());
            java.util.Set<String> group = view.get(5);
            String second = new String("other");
            for (int i = 0; i < 10000; i++)
            {
                tr.update(second);
            }
            assertEquals(1, extractions.get());
            assertEquals(2, group.size());
            assertEquals(2, extractions.get());
            tr.checkpoint();
            tr.commit();
        }
    }

    @Test
    void deferredExtractorFailureRejectsCheckpointAndLeavesOnlyExplicitCleanup()
    {
        IndexDefinition<MultiIndex<Integer, String>> words = IndexDefinition.multi("words", String.class, value -> {
            if (value.startsWith("bad"))
            {
                throw new IllegalArgumentException("invalid source");
            }
            return value.length();
        });
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure().context("/deferred-failure").index(words);
            Tr tr = ex.begin("/deferred-failure");
            MultiIndex<Integer, String> view = tr.index(words);
            tr.create("valid");
            tr.create("bad-value");
            ExistentialException failure = assertThrows(ExistentialException.class, () -> tr.checkpoint());
            assertTrue(failure.getMessage().contains("words"));
            assertThrows(NotFoundException.class, () -> ex.transactions().tr(tr.id()));
            assertThrows(ExistentialException.class, () -> view.size());
            assertThrows(ExistentialException.class, () -> tr.update("corrected"));
            tr.rollback();
        }
    }

}
