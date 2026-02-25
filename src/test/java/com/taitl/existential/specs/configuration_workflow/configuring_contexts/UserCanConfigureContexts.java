package com.taitl.existential.specs.configuration_workflow.configuring_contexts;

import com.taitl.ex.examples.night_city.model.Cat;
import com.taitl.existential.Ex;
import com.taitl.existential.configs.Context;
import com.taitl.existential.configs.Transaction;
import com.taitl.existential.specs.SpecBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class UserCanConfigureContexts extends SpecBase
{
    static class GlobalContext extends Context
    {
        GlobalContext(String op)
        {
            super(op);
        }
    }

    static class SpecificContext extends Context
    {
        SpecificContext(String op)
        {
            super(op);
        }
    }

    {
        autoConfigure = false;
    }

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
    @DisplayName("Configuring contexts")
    void configuringContexts()
    {
        assertDoesNotThrow(() -> {
            // @formatter:off
            Ex.configure("/api/cats")
                .context()
                    .invariant(Cat.class)
                    .create(v -> true, "ok")
                    .done()
                    .build()
                .context("/api/cats/create")
                    .invariant(Cat.class)
                    .create(v -> true, "ok")
                    .done()
                    .build();
            // @formatter:on

            String tran = ex.begin("/api/cats/create").id();
            ex.event("ok", tran);
            ex.commit(tran);
        });
    }

    @Test
    @DisplayName("Configuring contexts - parent rules apply and execute before child rules")
    void parentRulesApplyAndExecuteBeforeChildRules()
    {
        List<String> effectOrder = new ArrayList<>();

        // @formatter:off
        Ex.configure("/api/cats")
            .context()
                .effect(Cat.class)
                .create(v -> effectOrder.add("parent"))
                .done()
                .build()
            .context("/api/cats/create")
                .effect(Cat.class)
                .create(v -> effectOrder.add("child"))
                .done()
                .build();
        // @formatter:on

        assertDoesNotThrow(() -> {
            String tran = ex.begin("/api/cats/create").id();
            ex.event("ok", tran);
            ex.commit(tran);
        });

        assertEquals(List.of("parent", "child"), effectOrder);
    }

    @Test
    @DisplayName("Configuring contexts - custom context factories")
    void customContextFactories()
    {
        AtomicReference<Class<?>> rootType = new AtomicReference<>();
        AtomicReference<Class<?>> specificType = new AtomicReference<>();

        assertDoesNotThrow(() -> {
            // @formatter:off
            Ex.configure("/api/cats")
                .contextFactory(() -> new GlobalContext("/unused"))
                .context()
                    .invariant(Cat.class)
                    .create(v -> true, "ok")
                    .done()
                    .transaction(() -> {
                        Transaction tr = new Transaction("/api/cats/create", "root");
                        tr.begin((Transaction current) -> rootType.set(current.context().getClass()));
                        return tr;
                    })
                    .build()
                .context("/api/cats/create")
                    .contextFactory(() -> new SpecificContext("/unused"))
                    .invariant(Cat.class)
                    .create(v -> true, "ok")
                    .done()
                    .transaction(() -> {
                        Transaction tr = new Transaction("/api/cats/create", "specific");
                        tr.begin((Transaction current) -> specificType.set(current.context().getClass()));
                        return tr;
                    })
                    .build();
            // @formatter:on

            String tran = ex.begin("/api/cats/create").id();
            ex.event("ok", tran);
            ex.commit(tran);
        });

        assertEquals(GlobalContext.class, rootType.get());
        assertEquals(SpecificContext.class, specificType.get());
    }

    @Test
    @DisplayName("Configuring contexts - wildcard contexts participate in validation")
    void wildcardContextsParticipateInValidation()
    {
        List<String> effectOrder = new ArrayList<>();

        // @formatter:off
        Ex.configure("/api/cats/create")
            .context("/api/*/create")
                .effect(Cat.class)
                .create(v -> effectOrder.add("wildcard"))
                .done()
                .build()
            .context()
                .effect(Cat.class)
                .create(v -> effectOrder.add("concrete"))
                .done()
                .build();
        // @formatter:on

        assertDoesNotThrow(() -> {
            String tran = ex.begin("/api/cats/create").id();
            ex.event("ok", tran);
            ex.commit(tran);
        });

        assertEquals(List.of("wildcard", "concrete"), effectOrder);
    }

    @Test
    @DisplayName("Configuring contexts - parent context cannot be added under child config")
    void parentContextCannotBeAddedUnderChildConfig()
    {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> {
                // @formatter:off
                    Ex.configure("/api/cats/create")
                        .context("/api/cats");
                    // @formatter:on
                });

        assertTrue(ex.getMessage().contains("must match"));
    }

    @Test
    @DisplayName("Configuring contexts - unrelated context name is rejected")
    void unrelatedContextNameIsRejected()
    {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> {
                // @formatter:off
                    Ex.configure("/api/cats/create")
                        .context("/admin/users");
                    // @formatter:on
                });

        assertTrue(ex.getMessage().contains("must match"));
    }

    @Test
    @DisplayName("Configuring contexts - cannot define empty context")
    void cannotDefineEmptyContext()
    {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> {
                // @formatter:off
                    Ex.configure("/api/cats/create")
                        .context()
                        .build();
                    // @formatter:on
                });

        assertTrue(ex.getMessage().contains("Cannot configure context without defining rules"));
    }
}
