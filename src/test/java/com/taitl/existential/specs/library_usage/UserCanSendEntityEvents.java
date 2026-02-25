package com.taitl.existential.specs.library_usage;

import com.taitl.ex.examples.night_city.model.Cat;
import com.taitl.existential.keys.TypeKey;
import com.taitl.existential.specs.SpecBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        String tran = ex.begin(op).id();
        ex.event(null, cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can send an entity event using a type key")
    void sendEntityEventWithTypeKey() throws Exception
    {
        String tran = ex.begin(op).id();
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