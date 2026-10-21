package com.taitl.specs.library_usage;

import com.taitl.existential.transactions.Tr;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.specs.*;
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
                Tr tranTr = ex.begin(op);
                String tran = tranTr.id();
                ex.update(cat, tran);
            });
        }

        @Test
        @DisplayName("User can configure rules using fluent style")
        void configureWithFluentStyle()
        {
            assertDoesNotThrow(() -> {
                configureWithInstances();
                Tr tranTr = ex.begin(op);
                String tran = tranTr.id();
                ex.update(cat, tran);
            });
        }

        @Test
        @DisplayName("User can configure rules using builders")
        void configureWithBuilders()
        {
            assertDoesNotThrow(() -> {
                configure();
                Tr tranTr = ex.begin(op);
                String tran = tranTr.id();
                ex.update(cat, tran);
            });
        }

        @Test
        @DisplayName("User can configure rules mixing fluent style and builders")
        void configureMixingFluentAndBuilders()
        {
            assertDoesNotThrow(() -> {
                UserCanConfigureEntities.this.configureMixingFluentAndBuilders();
                Tr tranTr = ex.begin(op);
                String tran = tranTr.id();
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
                Tr tranTr = ex.begin(op);
                String tran = tranTr.id();
                List<String> values = new ArrayList<>(List.of("ok"));
                ex.port(null, values, typeKey, tran);
                tranTr.commit();
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

            Tr tranTr = ex.begin(op);
            String tran = tranTr.id();
            ex.create(cat, tran);
            ex.create(cat, tran);
            tranTr.commit();

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

            Tr tranTr = ex.begin(op);
            String tran = tranTr.id();
            ex.create(cat, tran);
            assertDoesNotThrow(() -> tranTr.commit());
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

            Tr tranTr = ex.begin(op);
            String tran = tranTr.id();
            ex.create(cat, tran);
            tranTr.checkpoint();
            assertEquals(1, checkpointCalls.get());

            tranTr.commit();
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

            Tr committedTr = ex.begin(op);
            String committed = committedTr.id();
            ex.create(cat, committed);
            committedTr.commit();
            assertEquals(0, rollbackCalls.get());

            Tr rolledBackTr = ex.begin(op);
            String rolledBack = rolledBackTr.id();
            ex.create(cat, rolledBack);
            rolledBackTr.rollback();
            assertEquals(1, rollbackCalls.get());
        }

        @Test
        @DisplayName("A rejected commit can be explicitly rolled back by its transaction handle")
        void rejectedCommitCanBeRolledBack() throws Exception
        {
            AtomicInteger rollbackCalls = new AtomicInteger();
            // @formatter:off
            ex.configure()
                    .context(op)
                    .invariant(cat.getClass())
                        .create(c -> "Black".equals(c.color), "Cats are born black")
                    .rollback()
                        .effect(cat.getClass())
                            .create(c -> rollbackCalls.incrementAndGet(), "rollback effect");
            // @formatter:on

            Tr tr = ex.begin(op);
            ex.create(CityTestData.GREY_CAT, tr.id());
            assertThrows(ValidationStageExceptions.class, () -> ex.commit(tr));
            assertEquals(0, rollbackCalls.get(), "rollback effects run only after explicit rollback");
            assertThrows(ExistentialException.class, () -> tr.create(cat),
                    "failed commit prevents additional work on the handle");

            assertThrows(NotFoundException.class, () -> ex.create(cat, tr.id()),
                    "failed commit detaches the ID from event lookup");
            ex.rollback(tr);
            assertEquals(1, rollbackCalls.get());
            assertThrows(ExistentialException.class, tr::rollback,
                    "rollback disposes the transaction handle");

            Tr next = ex.begin(op);
            ex.create(cat, next.id());
            assertDoesNotThrow(next::commit, "a rejected report must not contaminate a later validation");
        }

        @Test
        @DisplayName("A failing rollback callback still releases transaction state")
        void rollbackFailureStillDisposesTransaction() throws Exception
        {
            // @formatter:off
            ex.configure()
                    .context(op)
                    .rollback()
                        .effect(cat.getClass())
                            .create(c -> {
                                throw new IllegalStateException("rollback callback failed");
                            }, "failing rollback effect");
            // @formatter:on

            Tr tr = ex.begin(op);
            ex.create(cat, tr.id());
            assertThrows(EventHandlerException.class, tr::rollback);
            assertThrows(NotFoundException.class, () -> ex.create(cat, tr.id()));
            assertThrows(ExistentialException.class, tr::rollback);
        }
    }
}
