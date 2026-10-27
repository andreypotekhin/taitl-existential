package com.taitl.ex.logic.managed_indexes;

import com.taitl.ex.logic.library.*;
import com.taitl.existential.*;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.*;
import static org.junit.jupiter.api.Assertions.*;

class ManagedPolicySourcesTest
{
    @Test
    void loadsIndependentSettingsAndRejectsInvalidSourcesAtomically()
    {
        try (Existential ex = new Existential())
        {
            load(ex, "indexing.automatic=false\nindexing.enrollReads=false\n");
            assertFalse(ex.indexingPolicy().automatic());
            assertFalse(ex.indexingPolicy().enrollReads());
            assertThrows(IllegalStateException.class,
                    () -> load(ex, "indexing.automatic=true\nindexing.enrollReads=bad\n"));
            assertFalse(ex.indexingPolicy().automatic());
            assertFalse(ex.indexingPolicy().enrollReads());
            ex.indexingPolicy(IndexingPolicy.inherit().automatic(true));
            assertTrue(ex.indexingPolicy().automatic());
            assertFalse(ex.indexingPolicy().enrollReads());
        }
    }

    private void load(Existential ex, String text)
    {
        ClassLoader loader = new ClassLoader() {
            public InputStream getResourceAsStream(String name)
            {
                return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
            }
        };
        new ConfigureLibrary(ex, name -> null, loader).fromClasspath("managed.properties");
    }
}
