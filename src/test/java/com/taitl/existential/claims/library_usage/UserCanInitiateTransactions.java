package com.taitl.existential.claims.library_usage;

import com.taitl.existential.claims.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class UserCanInitiateTransactions extends ClaimBase
{
    @BeforeEach
    public void setup()
    {
        super.setup();
    }

    @AfterEach
    public void cleanup()
    {
        super.cleanup();
    }

    @Test
    @DisplayName("User can start a transaction")
    void begin() throws Exception
    {
        String tran = ex.begin(op);
        assertThat(tran, is(not(emptyString())));
    }

    @Test
    @DisplayName("User can commit transaction")
    void commit() throws Exception
    {
        String tran = ex.begin(op);
        ex.event(cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can rollback transaction")
    void rollback() throws Exception
    {
        String tran = ex.begin(op);
        ex.event(cat, tran);
        ex.rollback(tran);
    }

    @Test
    @DisplayName("User can use checkpoint to manually trigger validation")
    void checkpoint() throws Exception
    {
        String tran = ex.begin(op);
        ex.event(cat, tran);
        ex.checkpoint(tran);
    }
}