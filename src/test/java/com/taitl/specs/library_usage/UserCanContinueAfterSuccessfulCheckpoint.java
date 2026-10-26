package com.taitl.specs.library_usage;

import com.taitl.existential.constants.*;
import com.taitl.existential.transactions.*;
import com.taitl.specs.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanContinueAfterSuccessfulCheckpoint extends SpecBase
{
    {
        autoConfigure = false;
    }

    @BeforeEach
    void setUp()
    {
        super.setup();
    }

    @AfterEach
    void tearDown()
    {
        super.cleanup();
    }

    @Test
    void immediateIntentsRemainActiveAndBeginKeysRemainFolded()
    {
        AtomicInteger immediateCalls = new AtomicInteger();
        AtomicInteger beginCalls = new AtomicInteger();
        // @formatter:off
        ex.configure()
                .context(op)
                    .intent(String.class)
                        .write(value -> immediateCalls.incrementAndGet() > 0)
                    .begin()
                        .invariant(String.class)
                            .create(value -> beginCalls.incrementAndGet() > 0)
                        .invariant(Integer.class)
                            .create(value -> beginCalls.incrementAndGet() > 0);
        // @formatter:on

        Tr tr = ex.begin(op);
        tr.create("same key");
        ex.write("same key", tr.id());
        int beforeCheckpoint = immediateCalls.get();
        tr.checkpoint();
        tr.create("same key");
        tr.create(7);
        ex.write("same key", tr.id());
        try
        {
            assertEquals(2, beginCalls.get(), "the old key stays folded and a new key is evaluated once");
            assertTrue(immediateCalls.get() > beforeCheckpoint,
                    "immediate authorization remains active after checkpoint");
        }
        finally
        {
            tr.close();
        }
    }
}
