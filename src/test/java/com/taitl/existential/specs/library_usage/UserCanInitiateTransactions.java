package com.taitl.existential.specs.library_usage;

import com.taitl.existential.specs.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class UserCanInitiateTransactions extends SpecBase
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
        String tran = ex.begin(op).id();
        assertThat(tran, is(not(emptyString())));
    }

    @Test
    @DisplayName("User can commit transaction")
    void commit() throws Exception
    {
        String tran = ex.begin(op).id();
        ex.change(cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can rollback transaction")
    void rollback() throws Exception
    {
        String tran = ex.begin(op).id();
        ex.change(cat, tran);
        ex.rollback(tran);
    }

    @Test
    @DisplayName("User can use checkpoint to manually trigger validation")
    void checkpoint() throws Exception
    {
        String tran = ex.begin(op).id();
        ex.change(cat, tran);
        ex.checkpoint(tran);
    }
}