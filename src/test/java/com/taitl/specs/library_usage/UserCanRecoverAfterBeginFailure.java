package com.taitl.specs.library_usage;

import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanRecoverAfterBeginFailure
{
    private static final String OP = "/api/begin-recovery";

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
    void failedBeginRemovesAndDisposesItsHandle() throws Exception
    {
        AtomicBoolean failFirstBegin = new AtomicBoolean(true);
        ex.configure()
                .context(OP)
                .transaction(() -> {
                    Transaction transaction = new Transaction(OP, "begin-recovery");
                    transaction.begin(current -> {
                        if (failFirstBegin.getAndSet(false))
                        {
                            throw new IllegalStateException("first begin fails");
                        }
                    });
                    return transaction;
                });

        assertThrows(ExistentialException.class, () -> ex.begin(OP));
        assertEquals(0, ex.transactions().logic().registry().size(), "failed begin must not retain a registry entry");

        Tr tr = ex.begin(OP);
        assertNotNull(tr);
        tr.rollback();
    }
}
