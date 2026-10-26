package com.taitl.specs.library_usage;

import com.taitl.existential.*;
import com.taitl.existential.configs.*;
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
    void failedCheckpointMakesHandleRollbackOnlyAndDetachesItsId()
    {
        AtomicInteger rollbackCalls = new AtomicInteger();
        Transaction custom = new Transaction(OP, "rollback callback");
        custom.rollback(tr -> rollbackCalls.incrementAndGet());
        // @formatter:off
        ex.configure()
                .context(OP)
                .checkpoint()
                    .invariant(String.class)
                        .create(value -> false, "checkpoint rejected");
        // @formatter:on

        Tr tr = ex.begin(OP, custom);
        tr.create("value");
        assertThrows(ExistentialExceptions.class, tr::checkpoint);

        assertThrows(ExistentialException.class, () -> tr.create("more"));
        assertThrows(ExistentialException.class, tr::checkpoint);
        assertThrows(ExistentialException.class, tr::commit);
        assertThrows(ExistentialException.class, () -> tr.memo("before", "live", String.class));
        assertThrows(NotFoundException.class, () -> ex.transactions().tr(tr.id()));
        assertEquals(0, rollbackCalls.get(), "checkpoint failure does not invoke rollback callbacks");

        assertDoesNotThrow(tr::rollback);
        assertEquals(1, rollbackCalls.get(), "explicit rollback invokes the callback once");
    }

    @Test
    void failedCheckpointCanBeAbandonedAndASeparateTransactionCanSucceed()
    {
        // @formatter:off
        ex.configure()
                .context(OP)
                .checkpoint()
                    .invariant(String.class)
                        .create(value -> false, "checkpoint rejected");
        // @formatter:on

        Tr failed = ex.begin(OP);
        failed.create("value");
        assertThrows(ExistentialExceptions.class, failed::checkpoint);
        assertDoesNotThrow(failed::close);

        Tr fresh = ex.begin(OP);
        assertDoesNotThrow(fresh::commit);
    }
}
