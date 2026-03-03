package com.taitl.existential.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.events.*;
import com.taitl.existential.Ex;
import com.taitl.existential.keys.*;
import com.taitl.existential.specs.*;
import com.taitl.existential.transactions.Tr;
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
        String tran = ex.begin(op).id();
        ex.transit(null, cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can send an entity event using a type key")
    void sendEntityEventWithTypeKey() throws Exception
    {
        String tran = ex.begin(op).id();
        ex.port(null, cat, new TypeKey<Cat>(Cat.class), tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can send a delete transition without a type key")
    void sendDeleteTransitionWithoutTypeKey() throws Exception
    {
        String tran = ex.begin(op).id();
        ex.port(cat, null, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can send a create event to library")
    void sendCreateEvent() throws Exception
    {
        String tran = ex.begin(op).id();
        ex.create(cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can send a delete event to library using a type key")
    void sendDeleteEventWithTypeKey() throws Exception
    {
        String tran = ex.begin(op).id();
        ex.delete(cat, new TypeKey<Cat>(Cat.class), tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can send a modify event to library")
    void sendModifyEvent() throws Exception
    {
        String tran = ex.begin(op).id();
        ex.update(cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can send an update event to library using static facade")
    void sendUpdateEventUsingStaticFacade() throws Exception
    {
        String tran = Ex.begin(op).id();
        Ex.update(cat, new TypeKey<Cat>(Cat.class), tran);
        Ex.commit(tran);
    }

    @Test
    @DisplayName("User can send entity events using transaction shortcuts")
    void sendEntityEventsUsingTransactionShortcuts() throws Exception
    {
        Tr tr = ex.begin(op);
        tr.create(cat);
        tr.update(cat, new TypeKey<Cat>(Cat.class));
        tr.transit(cat, cat);
        tr.port(cat, null);
        tr.delete(cat);
        tr.commit();
    }

    @Test
    @DisplayName("User can send raw events using transaction shortcuts")
    void sendRawEventsUsingTransactionShortcuts() throws Exception
    {
        Tr tr = ex.begin(op);
        tr.event(new Create<>(cat), cat, new TypeKey<Cat>(Cat.class));
        tr.event(new Port<>(null, cat), new TypeKey<Cat>(Cat.class));
        tr.commit();
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
