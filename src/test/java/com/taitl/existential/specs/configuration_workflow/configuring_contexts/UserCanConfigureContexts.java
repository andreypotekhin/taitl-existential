package com.taitl.existential.specs.configuration_workflow.configuring_contexts;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.specs.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

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
            Ex.configure()
                .context("/api/cats")
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
            ex.update("ok", tran);
            ex.commit(tran);
        });
    }

    @Test
    @DisplayName("Configuring contexts - parent rules apply and execute before child rules")
    void parentRulesApplyAndExecuteBeforeChildRules()
    {
        List<String> effectOrder = new ArrayList<>();

        // @formatter:off
        Ex.configure()
            .context("/api/cats")
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
            ex.update("ok", tran);
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
            Ex.configure()
                .contextFactory(() -> new GlobalContext("/unused"))
                .context("/api/cats")
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
            ex.update("ok", tran);
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
        Ex.configure()
            .context("/api/*/create")
                .effect(Cat.class)
                    .create(v -> effectOrder.add("wildcard"))
                .done()
            .build()
            .context("/api/cats/create")
                .effect(Cat.class)
                    .create(v -> effectOrder.add("concrete"))
                .done()
            .build();
        // @formatter:on

        assertDoesNotThrow(() -> {
            String tran = ex.begin("/api/cats/create").id();
            ex.update("ok", tran);
            ex.commit(tran);
        });
        assertEquals(List.of("wildcard", "concrete"), effectOrder);
    }

    @Test
    @DisplayName("Configuring contexts - child context builder rejects parent context name")
    void parentContextCannotBeAddedUnderChildConfig()
    {
        // @formatter:off
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> {
                Ex.configure()
                    .context("/api/cats/create")
                    .context("/api/cats");
            });
        // @formatter:on
        assertTrue(ex.getMessage().contains("must match"));
    }

    @Test
    @DisplayName("Configuring contexts - unrelated context names can be declared on the root builder")
    void unrelatedContextNameIsAcceptedOnRootBuilder()
    {
        assertDoesNotThrow(() -> {
            // @formatter:off
            Ex.configure()
                .context("/api/cats/create")
                    .invariant(String.class)
                    .create(v -> true, "ok")
                    .done()
                    .build()
                .context("/admin/users")
                    .invariant(String.class)
                    .create(v -> true, "ok")
                    .done()
                    .build();
            // @formatter:on
        });
    }

    @Test
    @DisplayName("Configuring contexts - cannot define empty context")
    void cannotDefineEmptyContext()
    {
        // @formatter:off
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> {
                Ex.configure()
                    .context("/api/cats/create")
                    .build();
            });
        // @formatter:on
        assertTrue(ex.getMessage().contains("Cannot configure context without defining rules"));
    }
}
