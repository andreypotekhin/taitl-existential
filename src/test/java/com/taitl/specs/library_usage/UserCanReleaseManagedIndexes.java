package com.taitl.specs.library_usage;

import com.taitl.ex.logic.managed_indexes.data.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class UserCanReleaseManagedIndexes
{
    @Test
    void retainedClosedViewsReleaseAllOwnedMembershipAndRejectQueries()
    {
        IndexDefinition<MultiIndex<Integer, String>> words =
                IndexDefinition.multi("words", String.class, value -> value.length());
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure().context("/release").index(words);
            Tr tr = ex.begin("/release");
            tr.read("seed");
            MultiIndex<Integer, String> view = tr.index(words);
            Set<String> group = view.get(4);
            ManagedBinding binding = tr.indexMaintenance().bindings().iterator().next();
            assertTrue(binding.left.retainedReferences() > 0);
            tr.rollback();
            assertEquals(0, binding.left.retainedReferences());
            assertThrows(ExistentialException.class, () -> view.get(4));
            assertThrows(ExistentialException.class, () -> group.contains("seed"));
            assertTrue(tr.indexMaintenance().bindings().isEmpty());
        }
    }

    @Test
    void independentConcurrentHandlesDoNotShareIndexContents() throws Exception
    {
        IndexDefinition<SingleIndex<Integer, String>> words =
                IndexDefinition.single("words", String.class, value -> value.length());
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure().context("/isolated").index(words);
            ExecutorService pool = Executors.newFixedThreadPool(2);
            CountDownLatch loaded = new CountDownLatch(2);
            try
            {
                Future<?> first = pool.submit(() -> exercise(ex, words, "first", loaded));
                Future<?> second = pool.submit(() -> exercise(ex, words, "other", loaded));
                first.get(5, TimeUnit.SECONDS);
                second.get(5, TimeUnit.SECONDS);
            }
            finally
            {
                pool.shutdownNow();
            }
        }
    }

    private void exercise(Existential ex, IndexDefinition<SingleIndex<Integer, String>> words,
            String value, CountDownLatch loaded)
    {
        Tr tr = ex.begin("/isolated");
        try
        {
            tr.read(value);
            loaded.countDown();
            assertTrue(loaded.await(3, TimeUnit.SECONDS));
            assertSame(value, tr.index(words).get(value.length()));
            tr.commit();
        }
        catch (InterruptedException failure)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(failure);
        }
        finally
        {
            tr.close();
        }
    }
}
