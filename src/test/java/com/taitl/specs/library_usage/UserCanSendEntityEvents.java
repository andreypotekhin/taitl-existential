package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.events.*;
import com.taitl.existential.Ex;
import com.taitl.existential.keys.*;
import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.specs.*;
import com.taitl.existential.transactions.Tr;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Nested
    class Scenarios
    {
        @Test
        @DisplayName("User can send an entity event to library")
        void sendEntityEvent() throws Exception
        {
            Tr tranTr = ex.begin(op);
            String tran = tranTr.id();
            Cat updated = new Cat(cat.color(), "library");
            ex.transit(cat, updated, tran);
            tranTr.commit();
        }

        @Test
        @DisplayName("User can send an entity event using a type key")
        void sendEntityEventWithTypeKey() throws Exception
        {
            Tr tranTr = ex.begin(op);
            String tran = tranTr.id();
            ex.port(null, cat, new TypeKey<Cat>(Cat.class), tran);
            tranTr.commit();
        }

        @Test
        @DisplayName("User can send a delete transition without a type key")
        void sendDeleteTransitionWithoutTypeKey() throws Exception
        {
            Tr tranTr = ex.begin(op);
            String tran = tranTr.id();
            ex.port(cat, null, tran);
            tranTr.commit();
        }

        @Test
        @DisplayName("User can send a create event to library")
        void sendCreateEvent() throws Exception
        {
            Tr tranTr = ex.begin(op);
            String tran = tranTr.id();
            ex.create(cat, tran);
            tranTr.commit();
        }

        @Test
        @DisplayName("User can send a delete event to library using a type key")
        void sendDeleteEventWithTypeKey() throws Exception
        {
            Tr tranTr = ex.begin(op);
            String tran = tranTr.id();
            ex.delete(cat, new TypeKey<Cat>(Cat.class), tran);
            tranTr.commit();
        }

        @Test
        @DisplayName("User can send a modify event to library")
        void sendModifyEvent() throws Exception
        {
            Tr tranTr = ex.begin(op);
            String tran = tranTr.id();
            ex.update(cat, tran);
            tranTr.commit();
        }

        @Test
        @DisplayName("User can send an update event to library using static facade")
        void sendUpdateWithStaticFacade() throws Exception
        {
            Tr tranTr = Ex.begin(op);
            String tran = tranTr.id();
            Ex.update(cat, new TypeKey<Cat>(Cat.class), tran);
            tranTr.commit();
        }

        @Test
        @DisplayName("User can send entity events using transaction shortcuts")
        void sendEntityEventsWithTransactionShortcuts() throws Exception
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
        void sendRawEventsWithTransactionShortcuts() throws Exception
        {
            Tr tr = ex.begin(op);
            tr.event(new Create<>(cat), cat, new TypeKey<Cat>(Cat.class));
            tr.event(new Port<>(null, cat), new TypeKey<Cat>(Cat.class));
            tr.commit();
        }

        @Test
        @DisplayName("User can't send events outside a transaction")
        void sendingEventsOutsideTransaction() throws Exception
        {
            Tr tr = ex.begin(op);
            tr.commit();
            assertThat(assertThrows(ExistentialException.class, () -> tr.create(cat)).getMessage(),
                    containsString("/Troubleshooting.md#diagnostic-transaction-closed"));
        }
    }
}
