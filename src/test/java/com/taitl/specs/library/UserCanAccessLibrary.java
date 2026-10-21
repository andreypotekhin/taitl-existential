package com.taitl.specs.library;

import com.taitl.existential.transactions.Tr;
import com.taitl.existential.Ex;
import com.taitl.specs.SpecBase;
import org.junit.jupiter.api.*;

class UserCanAccessLibrary extends SpecBase
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

    @Nested
    class Scenarios
    {
        @Test
        @DisplayName("User can access library using static facade")
        void accessWithStaticFacade() throws Exception
        {
            Tr tranTr = Ex.begin(op);
            String tran = tranTr.id();
            tranTr.commit();
        }

        @Test
        @DisplayName("User can access library using a singleton")
        void accessWithSingleton() throws Exception
        {
            Tr tranTr = ex.begin(op);
            String tran = tranTr.id();
            tranTr.commit();
        }
    }

    // TODO
    // @Test
    // @DisplayName("independently configure and use multiple instances of Existential library")
    // TODO
    // Within an instance of Existential library, the user can configure multiple business
    // operations
    // Within a business operation configuration, the user can configure multiple operation contexts
    // Within an operation context, the user can configure multiple rules such as invariants,
    // effects and intents
}
