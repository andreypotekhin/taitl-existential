package com.taitl.existential.claims.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.claims.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class UserCanUseLibrary extends ClaimBase
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
    @DisplayName("User can access library")
    void accessLibrary() throws Exception
    {
        String tran = ex.begin(op);
        ex.commit(tran);
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

    @Test
    @DisplayName("User can begin transaction")
    void begin() throws Exception
    {
        String tran = ex.begin(op);
        assertThat(tran, is(not(emptyString())));
    }

    @Test
    @DisplayName("User can commit transaction")
    void commit() throws Exception
    {
        String tran = ex.begin(op);
        ex.event(cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can rollback transaction")
    void rollback() throws Exception
    {
        String tran = ex.begin(op);
        ex.event(cat, tran);
        ex.rollback(tran);
    }

    @Test
    @DisplayName("User can use checkpoint to manually trigger validation")
    void checkpoint() throws Exception
    {
        String tran = ex.begin(op);
        ex.event(cat, tran);
        ex.checkpoint(tran);
    }
}