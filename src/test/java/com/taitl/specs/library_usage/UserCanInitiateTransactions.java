package com.taitl.specs.library_usage;

import com.taitl.existential.transactions.Tr;
import com.taitl.existential.*;
import com.taitl.specs.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Tr tranTr = ex.begin(op);
        String tran = tranTr.id();
        assertThat(tran, is(not(emptyString())));
        tranTr.close();
    }

    @Test
    @DisplayName("Lifecycle methods require the transaction handle")
    void lifecycleMethodsDoNotAcceptStringIds()
    {
        for (Class<?> facade : new Class<?>[] { Ex.class, Existential.class })
        {
            assertThrows(NoSuchMethodException.class, () -> facade.getMethod("commit", String.class));
            assertThrows(NoSuchMethodException.class, () -> facade.getMethod("checkpoint", String.class));
            assertThrows(NoSuchMethodException.class, () -> facade.getMethod("rollback", String.class));
        }
    }

    @Test
    @DisplayName("User can commit transaction")
    void commit() throws Exception
    {
        Tr tranTr = ex.begin(op);
        String tran = tranTr.id();
        ex.update(cat, tran);
        tranTr.commit();
    }

    @Test
    @DisplayName("User can rollback transaction")
    void rollback() throws Exception
    {
        Tr tranTr = ex.begin(op);
        String tran = tranTr.id();
        ex.update(cat, tran);
        tranTr.rollback();
    }

    @Test
    @DisplayName("User can use checkpoint to manually trigger validation")
    void checkpoint() throws Exception
    {
        Tr tranTr = ex.begin(op);
        String tran = tranTr.id();
        ex.update(cat, tran);
        tranTr.checkpoint();
        tranTr.close();
    }
}
