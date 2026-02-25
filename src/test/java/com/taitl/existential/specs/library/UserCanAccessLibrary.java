package com.taitl.existential.specs.library;

import com.taitl.existential.Ex;
import com.taitl.existential.specs.SpecBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @Test
    @DisplayName("User can access library using static facade")
    void accessLibraryWithStaticFacade() throws Exception
    {
        String tran = Ex.begin(op).id();
        Ex.commit(tran);
    }

    @Test
    @DisplayName("User can access library using a singleton")
    void accessLibraryWithSingleton() throws Exception
    {
        String tran = ex.begin(op).id();
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