package com.taitl.existential.specs.library_usage;

import com.taitl.existential.keys.*;
import com.taitl.existential.specs.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanConfigureEntities extends SpecBase
{
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

    void configureWithInstances()
    {
        fixt.configureWithInstances();
    }

    void configureMixingFluentAndBuilders()
    {
        fixt.configureMixingFluentAndBuilders();
    }

    @Nested
    class Scenarios
    {
        @Test
        @DisplayName("User can configure class rules")
        void configureRules()
        {
            assertDoesNotThrow(() -> {
                configure();
                String tran = ex.begin(op).id();
                ex.update(cat, tran);
            });
        }

        @Test
        @DisplayName("User can configure rules using fluent style")
        void configureWithFluentStyle()
        {
            assertDoesNotThrow(() -> {
                configureWithInstances();
                String tran = ex.begin(op).id();
                ex.update(cat, tran);
            });
        }

        @Test
        @DisplayName("User can configure rules using builders")
        void configureWithBuilders()
        {
            assertDoesNotThrow(() -> {
                configure();
                String tran = ex.begin(op).id();
                ex.update(cat, tran);
            });
        }

        @Test
        @DisplayName("User can configure rules mixing fluent style and builders")
        void configureMixingFluentAndBuilders()
        {
            assertDoesNotThrow(() -> {
                configureMixingFluentAndBuilders();
                String tran = ex.begin(op).id();
                ex.update(cat, tran);
            });
        }

        @Test
        @DisplayName("User can configure class rules with a TypeKey for generic type")
        void configureWithTypeKeyForGenericType()
        {
            AtomicInteger effectCalls = new AtomicInteger();
            AtomicInteger invariantChecks = new AtomicInteger();
            TypeKey<List<String>> typeKey = new TypeKey<List<String>>() {
            };

            assertDoesNotThrow(() -> {
                ex.configure()
                        .context(op)
                        .invariant(typeKey)
                        .create(v -> {
                            invariantChecks.incrementAndGet();
                            return !v.isEmpty();
                        }, "require at least one value")
                        .effect(typeKey)
                        .create(v -> effectCalls.incrementAndGet(), "track list creates");
                String tran = ex.begin(op).id();
                List<String> values = new ArrayList<>(List.of("ok"));
                ex.port(null, values, typeKey, tran);
                ex.commit(tran);
            });

            assertEquals(1, effectCalls.get());
            assertEquals(1, invariantChecks.get());
        }

        @Test
        @DisplayName("Execution stages evaluate begin once and immediate on each event")
        void executionStages() throws Exception
        {
            AtomicInteger beginCalls = new AtomicInteger();
            AtomicInteger immediateCalls = new AtomicInteger();
            AtomicInteger validationCalls = new AtomicInteger();

            // @formatter:off
            ex.configure()
                    .context(op)
                    .begin()
                        .effect(cat.getClass())
                            .create(c -> beginCalls.incrementAndGet(), "begin create")
                    .immediate()
                        .effect(cat.getClass())
                            .create(c -> immediateCalls.incrementAndGet(), "immediate create")
                    .validation()
                        .effect(cat.getClass())
                            .create(c -> validationCalls.incrementAndGet(), "validation create");
            // @formatter:on

            String tran = ex.begin(op).id();
            ex.create(cat, tran);
            ex.create(cat, tran);
            ex.commit(tran);

            assertEquals(1, beginCalls.get());
            assertEquals(2, immediateCalls.get());
            assertEquals(1, validationCalls.get());
        }

        @Test
        @DisplayName("Commit stage is evaluated immediately before validation")
        void commitStageBeforeValidation() throws Exception
        {
            AtomicBoolean commitDone = new AtomicBoolean();

            // @formatter:off
            ex.configure()
                    .context(op)
                    .commit()
                        .effect(cat.getClass())
                            .create(c -> commitDone.set(true), "commit effect")
                    .validation()
                        .invariant(cat.getClass())
                            .create(c -> commitDone.get(), "commit should run before validation");
            // @formatter:on

            String tran = ex.begin(op).id();
            ex.create(cat, tran);
            assertDoesNotThrow(() -> ex.commit(tran));
            assertTrue(commitDone.get());
        }

        @Test
        @DisplayName("Checkpoint stage is evaluated on checkpoint and not on commit")
        void checkpointStageOnlyOnCheckpoint() throws Exception
        {
            AtomicInteger checkpointCalls = new AtomicInteger();

            // @formatter:off
            ex.configure()
                    .context(op)
                    .checkpoint()
                        .effect(cat.getClass())
                            .create(c -> checkpointCalls.incrementAndGet(), "checkpoint effect");
            // @formatter:on

            String tran = ex.begin(op).id();
            ex.create(cat, tran);
            ex.checkpoint(tran);
            assertEquals(1, checkpointCalls.get());

            ex.commit(tran);
            assertEquals(1, checkpointCalls.get());
        }

        @Test
        @DisplayName("Rollback stage is evaluated only on rollback")
        void rollbackStageOnlyOnRollback() throws Exception
        {
            AtomicInteger rollbackCalls = new AtomicInteger();

            // @formatter:off
            ex.configure()
                    .context(op)
                    .rollback()
                        .effect(cat.getClass())
                            .create(c -> rollbackCalls.incrementAndGet(), "rollback effect");
            // @formatter:on

            String committed = ex.begin(op).id();
            ex.create(cat, committed);
            ex.commit(committed);
            assertEquals(0, rollbackCalls.get());

            String rolledBack = ex.begin(op).id();
            ex.create(cat, rolledBack);
            ex.rollback(rolledBack);
            assertEquals(1, rollbackCalls.get());
        }
    }
}
