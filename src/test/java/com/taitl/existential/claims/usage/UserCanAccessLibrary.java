package com.taitl.existential.claims.usage;

import com.taitl.existential.Existential;
import com.taitl.existential.contexts.Context;
import com.taitl.existential.invariants.Effect;
import com.taitl.existential.invariants.Invariant;
import com.taitl.existential.keys.TypeKey;
import com.taitl.existential.model.cats.Cat;
import com.taitl.existential.model.cats.Location;
import com.taitl.existential.model.cats.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserCanAccessLibrary
{
    Existential ex;
    String op;
    Cat cat;

    @BeforeEach
    void setup()
    {
        ex = new Existential();
        op = "/api/cats";
        cat = new Cat(TestData.BLACK_CAT.color(), TestData.BLACK_CAT.location());
    }

    @AfterEach
    void cleanup()
    {
        ex.close();
    }

    void configure()
    {
        ex.contexts.configure(op)
                .context(() -> new Context(op) {
                    {
                        require(new Invariant<Cat>() {
                            {
                                create(c -> "Black".equals(c.color), "Cats are born black");
                            }
                        });
                        require(new Effect<Cat>() {
                            {
                                create(c -> c.location = new Location("Park"), "Set location for all new cats");
                            }
                        });
                    }
                });
    }

    @Test
    @DisplayName("User can access library")
    void accessLibrary() throws Exception
    {
        configure();
        String tran = ex.transactions.begin(op);
        ex.transactions.commit(tran);
    }

    @Test
    @DisplayName("User can send entity event to library")
    void sendEntityEvent() throws Exception
    {
        configure();
        String tran = ex.transactions.begin(op);
        ex.events.send(null, cat, new TypeKey<Cat>(Cat.class), tran);
        ex.transactions.commit(tran);
    }

    @Test
    @DisplayName("User can record access to entity")
    void recordEntityAccess() throws Exception
    {
        configure();
        String tran = ex.transactions.begin(op);
        ex.access.read(cat, new TypeKey<Cat>(Cat.class), tran);
        ex.access.write(cat, tran);
        ex.transactions.commit(tran);
    }

    @Test
    @DisplayName("User can rollback transaction")
    void commit() throws Exception
    {
        configure();
        String tran = ex.transactions.begin(op);
        ex.events.send(cat, tran);
        ex.transactions.commit(tran);
    }

    @Test
    @DisplayName("User can rollback transaction")
    void rollback() throws Exception
    {
        configure();
        String tran = ex.transactions.begin(op);
        ex.events.send(cat, tran);
        ex.transactions.rollback(tran);
    }

    @Test
    @DisplayName("User can use checkpoint to pre-commit transaction")
    void checkpoint() throws Exception
    {
        configure();
        String tran = ex.transactions.begin(op);
        ex.events.send(cat, tran);
        ex.transactions.checkpoint(tran);
    }

    @Test
    @DisplayName("User can't sent events to library which hasn't been configured")
    void sendEventsToUnconfiguredLibrary() throws Exception
    {
        assertThat(assertThrows(IllegalStateException.class, () -> {
            String tran = ex.transactions.begin(op);
            ex.events.send(cat, tran);
        }).getMessage(), containsString("You need to configure at least one context"));
    }

    @Test
    @DisplayName("User can't sent events to library before it is configured")
    void sendEventsToLibraryBeforeItIsConfigured() throws Exception
    {
        assertThat(assertThrows(IllegalStateException.class, () -> {
            ex.transactions.begin(op);
            configure();
        }).getMessage(), containsString("You need to configure at least one context"));
    }
}