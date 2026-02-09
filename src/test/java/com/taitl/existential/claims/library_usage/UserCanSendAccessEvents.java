package com.taitl.existential.claims.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.claims.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

class UserCanSendAccessEvents extends ClaimBase
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
    @DisplayName("User can record access to entity")
    void recordEntityAccess() throws Exception
    {
        String tran = ex.begin(op);
        ex.read(cat, tran);
        ex.write(cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can record access to entity using type key")
    void recordEntityAccessWithTypeKey() throws Exception
    {
        String tran = ex.begin(op);
        ex.read(cat, new TypeKey<Cat>(Cat.class), tran);
        ex.write(cat, tran);
        ex.commit(tran);
    }
}