package com.taitl.existential.claims.library;

import com.taitl.existential.*;
import com.taitl.existential.claims.*;
import org.junit.jupiter.api.*;

class UserCanAccessLibrary extends ClaimBase
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
    @DisplayName("User can access library using static facade")
    void accessLibraryWithStaticFacade() throws Exception
    {
        String tran = Ex.begin(op);
        Ex.commit(tran);
    }

    @Test
    @DisplayName("User can access library using a singleton")
    void accessLibraryWithSingleton() throws Exception
    {
        String tran = ex.begin(op);
        ex.commit(tran);
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