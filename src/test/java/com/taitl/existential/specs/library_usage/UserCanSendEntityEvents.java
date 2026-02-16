package com.taitl.existential.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.specs.*;
import org.junit.jupiter.api.*;

class UserCanSendEntityEvents extends SpecBase
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
    @DisplayName("User can send an entity event to library")
    void sendEntityEvent() throws Exception
    {
        String tran = ex.begin(op);
        ex.event(null, cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can send an entity event using a type key")
    void sendEntityEventWithTypeKey() throws Exception
    {
        String tran = ex.begin(op);
        ex.event(null, cat, new TypeKey<Cat>(Cat.class), tran);
        ex.commit(tran);
    }

    // TODO
    // @Test
    // @DisplayName("User can't send events outside a transaction")
    // void sendingEventsOutsideTransaction()
    // {
    // assertThat(assertThrows(IllegalStateException.class, () -> {
    // String tran = ex.begin(op);
    // ex.commit(tran);
    // ex.event(cat, tran);
    // }).getMessage(), containsString("You cannot send events outside a transaction"));
    // }
}