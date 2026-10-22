package com.taitl.specs.library_usage;

import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanRecoverAfterCheckpointFailure
{
    private static final String OP = "/api/checkpoint-recovery";

    private Existential ex;

    @BeforeEach
    void setUp()
    {
        ex = new Existential();
    }

    @AfterEach
    void tearDown()
    {
        ex.close();
    }

    @Test
    void failedCheckpointLeavesHandleUsableForNewWorkAndRetry() throws Exception
    {
        AtomicInteger evaluations = new AtomicInteger();
        // @formatter:off
        ex.configure()
                .context(OP)
                .checkpoint()
                    .invariant(String.class)
                        .create(value -> evaluations.incrementAndGet() > 1,
                                "the first checkpoint attempt is rejected");
        // @formatter:on

        Tr tr = ex.begin(OP);
        try
        {
            tr.create("before");
            assertThrows(ExistentialExceptions.class, tr::checkpoint);
            tr.create("after");

            assertDoesNotThrow(tr::checkpoint, "a checkpoint retry can validate the still-active handle");
            assertDoesNotThrow(tr::commit);
        }
        finally
        {
            tr.close();
        }
    }
}
