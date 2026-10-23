package com.taitl.specs.configuration_workflow.configuring_contexts;

import com.taitl.existential.transactions.Tr;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;
import com.taitl.specs.*;
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

    @Nested
    class Scenarios
    {
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
                    .context("/api/cats/create")
                        .invariant(Cat.class)
                        .create(v -> true, "ok");
                // @formatter:on

                Tr tranTr = ex.begin("/api/cats/create");
                String tran = tranTr.id();
                ex.update("ok", tran);
                tranTr.commit();
            });
        }

        @Test
        @DisplayName("Configuring contexts - parent rules apply and execute before child rules")
        void parentRulesExecuteBeforeChildRules()
        {
            List<String> effectOrder = new ArrayList<>();

            // @formatter:off
            Ex.configure()
                .context("/api/cats")
                    .effect(Cat.class)
                        .create(v -> effectOrder.add("parent"))
                .context("/api/cats/create")
                    .effect(Cat.class)
                        .create(v -> effectOrder.add("child"));
            // @formatter:on

            assertDoesNotThrow(() -> {
                Tr tranTr = ex.begin("/api/cats/create");
                String tran = tranTr.id();
                ex.create(cat, tran);
                tranTr.commit();
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
                        .transaction(() -> {
                            Transaction tr = new Transaction("/api/cats/create", "root");
                            tr.begin((Transaction current) -> rootType.set(current.context().getClass()));
                            return tr;
                        })
                    .context("/api/cats/create")
                        .contextFactory(() -> new SpecificContext("/unused"))
                        .invariant(Cat.class)
                        .create(v -> true, "ok")
                        .transaction(() -> {
                            Transaction tr = new Transaction("/api/cats/create", "specific");
                            tr.begin((Transaction current) -> specificType.set(current.context().getClass()));
                            return tr;
                        })
                    ;
                // @formatter:on

                Tr tranTr = ex.begin("/api/cats/create");
                String tran = tranTr.id();
                ex.update("ok", tran);
                tranTr.commit();
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
                    .context("/api/cats/create")
                    .effect(Cat.class)
                        .create(v -> effectOrder.add("concrete"));
            // @formatter:on

            assertDoesNotThrow(() -> {
                Tr tranTr = ex.begin("/api/cats/create");
                String tran = tranTr.id();
                ex.create(cat, tran);
                tranTr.commit();
            });
            assertEquals(List.of("wildcard", "concrete"), effectOrder);
        }

        @Test
        @DisplayName("Parent levels run before children and same-level wildcards keep declaration order")
        void ordersOverlappingContextsByPathLevel() throws Exception
        {
            List<String> order = new ArrayList<>();
            ConfigBuilder builder = Ex.configure();
            // @formatter:off
            builder.context("/api/cats/save")
                    .transaction((op, name) -> {
                        order.add("factory-leaf");
                        return new Transaction(op, name);
                    })
                    .effect(Cat.class).create(cat -> order.add("leaf"))
                ;
            builder.context("/api/cats/*")
                    .transaction((op, name) -> {
                        order.add("factory-wildcard-two");
                        return new Transaction(op, name);
                    })
                    .effect(Cat.class).create(cat -> order.add("wildcard-two"))
                ;
            builder.context("/api/cats")
                    .transaction((op, name) -> {
                        order.add("factory-cats");
                        return new Transaction(op, name);
                    })
                    .effect(Cat.class).create(cat -> order.add("cats"))
                ;
            builder.context("/api/*/save")
                    .transaction((op, name) -> {
                        order.add("factory-wildcard-one");
                        return new Transaction(op, name);
                    })
                    .effect(Cat.class).create(cat -> order.add("wildcard-one"))
                ;
            builder.context("/api")
                    .transaction((op, name) -> {
                        order.add("factory-api");
                        return new Transaction(op, name);
                    })
                    .effect(Cat.class).create(cat -> order.add("api"))
                ;
            builder.context("/api/*")
                    .transaction((op, name) -> {
                        order.add("factory-parent-wildcard");
                        return new Transaction(op, name);
                    })
                    .effect(Cat.class).create(cat -> order.add("parent-wildcard"));
            // @formatter:on

            Tr tr = ex.begin("/api/cats/save");
            try
            {
                tr.create(cat);
                tr.commit();
            }
            finally
            {
                tr.close();
            }

            assertEquals(List.of("factory-api", "factory-parent-wildcard", "factory-cats", "factory-wildcard-two",
                    "factory-wildcard-one", "factory-leaf", "api", "parent-wildcard", "cats", "wildcard-two",
                    "wildcard-one", "leaf"), order);
        }

        @Test
        @DisplayName("Configuring contexts - child context builder rejects parent context name")
        void parentContextRejectedUnderChild()
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
        void unrelatedContextNameAcceptedOnRootBuilder()
        {
            assertDoesNotThrow(() -> {
                ConfigBuilder configBuilder = Ex.configure();

                // @formatter:off
                configBuilder.context("/api/cats/create")
                        .invariant(String.class)
                        .create(v -> true, "ok")
                    ;
                configBuilder.context("/admin/users")
                        .invariant(String.class)
                        .create(v -> true, "ok");
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
                        .context("/api/cats/create");
                    UserCanConfigureContexts.this.ex.begin("/api/cats/create");
                });
            // @formatter:on
            assertTrue(ex.getMessage().contains("Cannot configure context without defining rules"));
        }
    }
}
