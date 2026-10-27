package com.taitl.specs.library_usage;

import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;

class UserCanConfigureManagedIndexPolicies
{
    @Test
    void inheritsIndependentOptionsAcrossAppContextTransactionAndIndex()
    {
        IndexDefinition<SingleIndex<Integer, String>> words =
                IndexDefinition.single("words", String.class, value -> value.length());
        IndexDefinition<SingleIndex<Integer, String>> withReads =
                IndexDefinition.single("withReads", String.class, value -> value.length())
                        .withPolicy(IndexingPolicy.inherit().enrollReads(true));
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.indexingPolicy(IndexingPolicy.defaults().automatic(false));
            ex.configure().context("/app").indexingPolicy(IndexingPolicy.inherit().enrollReads(false));
            ex.configure()
                    .context("/app/save")
                    .index(words)
                    .index(withReads)
                    .transaction("runtime")
                    .indexingPolicy(IndexingPolicy.inherit().automatic(true));
            Tr tr = ex.begin("/app/save");
            tr.read("read");
            assertNull(tr.index(words).get(4));
            assertEquals("read", tr.index(withReads).get(4));
            tr.create("new");
            assertEquals("new", tr.index(words).get(3));
            tr.commit();
            assertThrows(IllegalStateException.class,
                    () -> ex.indexingPolicy(IndexingPolicy.defaults()));
        }
    }

    @Test
    void manualModeSkipsAutomaticExtractorsAndStillAcceptsSeedsAndExplicitChanges()
    {
        AtomicInteger calls = new AtomicInteger();
        IndexDefinition<SingleIndex<Integer, String>> words =
                IndexDefinition.single("words", String.class, value -> {
                    calls.incrementAndGet();
                    return value.length();
                }).withPolicy(IndexingPolicy.inherit().automatic(false));
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure().context("/manual").index(words);
            Tr tr = ex.begin("/manual");
            tr.read("ignored");
            tr.create("also ignored");
            assertEquals(0, calls.get());
            SingleIndex<Integer, String> index = tr.index(words);
            index.addAll(List.of("seed"));
            assertEquals(1, calls.get());
            index.put(8, "explicit");
            assertEquals("seed", index.get(4));
            assertEquals("explicit", index.get(8));
            tr.delete("seed");
            assertEquals("seed", index.get(4));
            tr.commit();
        }
    }

    @Test
    void suppliedTransactionOverridesContextAndItsDeclarationsAreIsolated()
    {
        IndexDefinition<SingleIndex<Integer, String>> words =
                IndexDefinition.single("words", String.class, value -> value.length());
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure()
                    .context("/custom")
                    .index(words)
                    .indexingPolicy(IndexingPolicy.inherit().automatic(false));
            Transaction custom = new Transaction("/custom", "invocation");
            custom.indexingPolicy(IndexingPolicy.inherit().automatic(true));
            Tr tr = ex.begin("/custom", custom);
            tr.read("loaded");
            assertEquals("loaded", tr.index(words).get(6));
            assertThrows(IllegalStateException.class,
                    () -> custom.indexingPolicy(IndexingPolicy.defaults()));
            tr.commit();
            Tr independent = ex.begin("/custom");
            independent.read("ignored");
            assertTrue(independent.index(words).isEmpty());
            independent.commit();
        }
    }
}
