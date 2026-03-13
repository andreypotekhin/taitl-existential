package com.taitl.existential.builders;

import com.taitl.existential.Existential;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

class ContextBuilderTest
{
    static class CustomTransaction extends Transaction
    {
        CustomTransaction()
        {
            super("/app", "custom");
        }
    }

    @Nested
    class Build
    {
        @Nested
        class ContextCases
        {
            @Test
            @DisplayName("Build attaches context to parent")
            void attachesToParent()
            {
                ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
                ContextBuilder contextBuilder = configBuilder.context("/app");
                com.taitl.existential.configs.Context context = new com.taitl.existential.configs.Context("/app");
                contextBuilder.contextFactory(() -> context);
                contextBuilder.invariant(new Invariant<>(String.class));

                contextBuilder.buildContext();

                assertEquals(1, configBuilder.contexts.size());
                assertSame(context, configBuilder.contexts.get(0));
            }

            @Test
            @DisplayName("Build on one context builds all pending contexts")
            void buildsAllPendingContexts()
            {
                ConfigBuilder configBuilder = new ConfigBuilder(new Existential());

                configBuilder.context("/app/one")
                        .invariant(String.class)
                        .create(v -> true, "ctx one");
                ContextBuilder contextBuilder = configBuilder.context("/app/two");
                contextBuilder.invariant(String.class)
                        .create(v -> true, "ctx two");

                configBuilder.buildContexts();

                List<String> names = new ArrayList<>();
                for (Context context : configBuilder.contexts)
                {
                    names.add(context.name());
                }
                assertEquals(List.of("/app/one", "/app/two"), names);
            }

            @Test
            @DisplayName("Repeated internal build calls are idempotent")
            void repeatedCalls()
            {
                ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
                ContextBuilder contextBuilder = configBuilder.context("/app");
                contextBuilder.invariant(String.class).create(v -> true, "rule");

                configBuilder.buildContexts();
                configBuilder.buildContexts();

                assertEquals(1, configBuilder.contexts.size());
            }
        }
    }

    @Nested
    class Contexts
    {
        @Nested
        class Siblings
        {
            @Test
            @DisplayName("Parameterless sibling context delegates to parent config op")
            void parameterlessDelegatesToParentConfigOp()
            {
                ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
                ContextBuilder contextBuilder = configBuilder.context("/app");

                ContextBuilder sibling = contextBuilder.context();

                assertEquals("/app", sibling.op);
            }

            @Test
            @DisplayName("Rule builders can short-circuit to sibling context")
            void ruleBuildersShortCircuitToSiblingContext()
            {
                ConfigBuilder configBuilder = new ConfigBuilder(new Existential());

                configBuilder.context("/app")
                        .invariant(String.class)
                        .create(v -> true, "ctx one")
                        .context("/app/two")
                        .effect(String.class)
                        .create(v -> {
                        }, "ctx two")
                        .context("/app/two/three")
                        .intent(String.class)
                        .read();

                configBuilder.buildContexts();

                List<String> names = new ArrayList<>();
                for (Context context : configBuilder.contexts)
                {
                    names.add(context.name());
                }
                assertEquals(List.of("/app", "/app/two", "/app/two/three"), names);
            }
        }

        @Nested
        class RuleOrder
        {
            @Test
            @DisplayName("Preserves context rule order")
            void preserves()
            {
                ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
                ContextBuilder contextBuilder = configBuilder.context("/app");
                Context context = new Context("/app");
                contextBuilder.contextFactory(() -> context);

                Invariant<String> inv1 = new Invariant<>(String.class);
                inv1.on(s -> true, "inv1");
                contextBuilder.invariant(inv1);

                // @formatter:off
                contextBuilder.invariant(String.class)
                    .create(s -> true, "inv2");
                // @formatter:on

                Effect<String> eff1 = new Effect<>(String.class);
                eff1.on(s -> {
                }, "eff1");
                contextBuilder.effect(eff1);

                Intent<String> intent1 = new Intent<>(String.class);
                intent1.read();
                contextBuilder.intent(intent1, TypeKey.valueOf(String.class, false));

                // @formatter:off
                contextBuilder.effect(String.class)
                    .create(s -> {
                    }, "eff2");
                contextBuilder.intent(String.class)
                    .write();
                // @formatter:on

                contextBuilder.buildContext();

                List<Evs<?>> begin = context.stage().at(StageName.BEGIN);
                List<Evs<?>> immediate = context.stage().at(StageName.IMMEDIATE);
                List<Evs<?>> validation = context.stage().at(StageName.VALIDATION);

                assertEquals(0, begin.size());
                assertEquals(2, immediate.size());
                assertEquals(4, validation.size());
                assertSame(inv1, validation.get(0));
                assertTrue(((Invariant<?>) validation.get(1)).list().get(0) instanceof OnCreate);
                assertSame(eff1, validation.get(2));
                assertTrue(((Effect<?>) validation.get(3)).list().get(0) instanceof OnCreate);
                assertSame(intent1, immediate.get(0));
                assertTrue(((Intent<?>) immediate.get(1)).list()
                        .get(0) instanceof com.taitl.existential.handlers.access_handlers.OnWrite);
            }
        }
    }

    @Nested
    class Transactions
    {
        @Nested
        class RuleOrder
        {
            @Test
            @DisplayName("Preserves transaction rule order")
            void preserves()
            {
                ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
                ContextBuilder contextBuilder = configBuilder.context("/app");
                TransactionBuilder transactionBuilder =
                        contextBuilder.transaction(() -> new Transaction("/app", "test"));

                Invariant<String> inv1 = new Invariant<>(String.class);
                inv1.on(s -> true, "inv1");
                transactionBuilder.invariant(inv1);

                transactionBuilder.begin((Transaction tr) -> {
                });

                // @formatter:off
                transactionBuilder.invariant(String.class)
                    .create(s -> true, "inv2")
                    .doneTran();
                // @formatter:on

                Effect<String> eff1 = new Effect<>(String.class);
                eff1.on(s -> {
                }, "eff1");
                transactionBuilder.effect(eff1);

                Intent<String> intent1 = new Intent<>(String.class);
                intent1.read();
                transactionBuilder.intent(intent1, TypeKey.valueOf(String.class, false));

                // @formatter:off
                transactionBuilder.effect(String.class)
                    .create(s -> {
                    }, "eff2")
                    .doneTran();
                transactionBuilder.intent(String.class)
                    .write()
                    .doneTran();
                // @formatter:on

                List<Supplier<? extends Evs<?>>> suppliers = transactionBuilder.evsSuppliers;
                assertEquals(7, suppliers.size());
                assertSame(inv1, suppliers.get(0).get());
                assertTrue(suppliers.get(1).get() instanceof Life);
                assertTrue(((Invariant<?>) suppliers.get(2).get()).list().get(0) instanceof OnCreate);
                assertSame(eff1, suppliers.get(3).get());
                assertSame(intent1, suppliers.get(4).get());
                assertTrue(((Effect<?>) suppliers.get(5).get()).list().get(0) instanceof OnCreate);
                assertTrue(((Intent<?>) suppliers.get(6).get()).list()
                        .get(0) instanceof com.taitl.existential.handlers.access_handlers.OnWrite);
            }
        }
    }

    @Nested
    class TypeKeys
    {
        @Nested
        class ContextBuilderOverloads
        {
            @Test
            @DisplayName("Context builder attaches type keys from class and type key overloads")
            void attachesFromClassAndTypeKeyOverloads()
            {
                ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
                ContextBuilder contextBuilder = configBuilder.context("/app");
                Context context = new Context("/app");
                contextBuilder.contextFactory(() -> context);
                TypeKey<List<String>> reflectionType = new TypeKey<List<String>>() {
                };
                TypeKey<String> stringType = new TypeKey<>("String");

                contextBuilder.invariant(String.class)
                        .create(s -> true, "inv")
                        .invariant(reflectionType)
                        .create(v -> true, "list inv")
                        .effect(stringType)
                        .create(s -> {
                        }, "eff")
                        .intent(String.class)
                        .read();

                contextBuilder.buildContext();

                List<Evs<?>> immediate = context.stage().at(StageName.IMMEDIATE);
                List<Evs<?>> validation = context.stage().at(StageName.VALIDATION);

                assertEquals(TypeKey.valueOf(String.class, false), validation.get(0).typeKey());
                assertEquals(reflectionType, validation.get(1).typeKey());
                assertEquals(stringType, validation.get(2).typeKey());
                assertEquals(TypeKey.valueOf(String.class, false), immediate.get(0).typeKey());
            }
        }

        @Nested
        class TransactionLifecycle
        {
            @Test
            @DisplayName("Transaction lifecycle overloads assign type key to life")
            void assignsTypeKeyToLife()
            {
                ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
                ContextBuilder contextBuilder = configBuilder.context("/app");
                TransactionBuilder transactionBuilder = contextBuilder.transaction(CustomTransaction::new);
                TypeKey<CustomTransaction> reflectionFullNameType = new TypeKey<CustomTransaction>(true) {
                };

                transactionBuilder.begin(CustomTransaction.class, tr -> {
                });
                transactionBuilder.commit(reflectionFullNameType, tr -> {
                });

                Life<?> beginCycle = (Life<?>) transactionBuilder.evsSuppliers.get(0).get();
                Life<?> commitCycle = (Life<?>) transactionBuilder.evsSuppliers.get(1).get();

                assertEquals(TypeKey.valueOf(CustomTransaction.class, false), beginCycle.typeKey());
                assertEquals(reflectionFullNameType, commitCycle.typeKey());
            }
        }
    }

    @Nested
    class Contracts
    {
        @Nested
        class EvsTypeKey
        {
            @Test
            @DisplayName("Evs type key contract is never null")
            void neverNull()
            {
                Invariant<String> invariant = new Invariant<>(String.class);
                Effect<String> effect = new Effect<>(new TypeKey<String>() {
                });
                Intent<String> intent = new Intent<>(String.class);
                Life<Transaction> life = new Life<>(Transaction.class);
                Invariant<List<String>> reflected = new Invariant<List<String>>() {
                };

                assertNotNull(invariant.typeKey());
                assertNotNull(effect.typeKey());
                assertNotNull(intent.typeKey());
                assertNotNull(life.typeKey());
                assertEquals(new TypeKey<>(String.class), invariant.typeKey());
                assertEquals(new TypeKey<String>() {
                }, effect.typeKey());
                assertEquals(new TypeKey<>(String.class), intent.typeKey());
                assertEquals(new TypeKey<Transaction>(Transaction.class), life.typeKey());
                assertEquals(new TypeKey<List<String>>() {
                }, reflected.typeKey());
            }
        }
    }

    @Nested
    class Stages
    {
        @Test
        @DisplayName("Context defaults route rules to validation and immediate stages")
        void contextDefaults()
        {
            Context context = new Context("/app");
            context.invariant(new Invariant<>(String.class));
            context.effect(new Effect<>(String.class));
            context.intent(new Intent<>(String.class));

            assertEquals(0, context.stage().at(StageName.BEGIN).size());
            assertEquals(1, context.stage().at(StageName.IMMEDIATE).size());
            assertEquals(2, context.stage().at(StageName.VALIDATION).size());
        }

        @Test
        @DisplayName("Transaction defaults route lifecycle rules to begin stage")
        void transactionDefaults()
        {
            Transaction transaction = new Transaction("/app", "tx");
            transaction.invariant(new Invariant<>(String.class));
            transaction.effect(new Effect<>(String.class));
            transaction.intent(new Intent<>(String.class));
            transaction.begin((Transaction tr) -> {
            });

            assertEquals(1, transaction.stage().at(StageName.BEGIN).size());
            assertEquals(1, transaction.stage().at(StageName.IMMEDIATE).size());
            assertEquals(2, transaction.stage().at(StageName.VALIDATION).size());
        }

        @Test
        @DisplayName("Transaction lifecycle methods pin handlers to lifecycle stages")
        void transactionLifecycleMethodsPinStages()
        {
            Transaction transaction = new Transaction("/app", "tx");

            transaction.validation();
            transaction.rollback((Transaction tr) -> {
            });

            assertEquals(1, transaction.stage().at(StageName.ROLLBACK).size());
            assertEquals(0, transaction.stage().at(StageName.VALIDATION).size());
        }

        @Test
        @DisplayName("Transaction lifecycle methods route cycles to lifecycle stages")
        void transactionLifecycleStageRouting()
        {
            ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
            TransactionBuilder builder = configBuilder.context("/app").transaction("tx");

            builder.begin((Transaction tr) -> {
            });
            builder.commit((Transaction tr) -> {
            });
            builder.checkpoint((Transaction tr) -> {
            });
            builder.rollback((Transaction tr) -> {
            });

            assertEquals(List.of(StageName.BEGIN, StageName.COMMIT, StageName.CHECKPOINT, StageName.ROLLBACK),
                    builder.evsStages);
        }

        @Test
        @DisplayName("Lifecycle methods pin handlers to their stage despite stage cursor")
        void lifecycleMethodsPinStage()
        {
            ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
            TransactionBuilder builder = configBuilder.context("/app").transaction("tx");

            builder.validation().rollback((Transaction tr) -> {
            });

            assertEquals(1, builder.evsStages.size());
            assertEquals(StageName.ROLLBACK, builder.evsStages.get(0));
        }

        @Test
        @DisplayName("Empty cycle fails when stage cannot be inferred")
        void emptyCycleFailsWhenUnstaged()
        {
            ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
            TransactionBuilder builder = configBuilder.context("/app").transaction("tx");

            assertThrows(IllegalArgumentException.class, () -> builder.cycle(new Life<>(Transaction.class)));
        }

        @Test
        @DisplayName("Cycle with multiple handlers is registered in matching lifecycle stages")
        void cycleWithMultipleHandlersStageRouting()
        {
            ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
            TransactionBuilder builder = configBuilder.context("/app").transaction("tx");
            Life<Transaction> life = new Life<>(Transaction.class);
            life.begin(tr -> {
            });
            life.commit(tr -> {
            });
            life.rollback(tr -> {
            });

            builder.cycle(life);

            assertEquals(List.of(StageName.BEGIN, StageName.COMMIT, StageName.ROLLBACK), builder.evsStages);
            assertSame(life, builder.evsSuppliers.get(0).get());
            assertSame(life, builder.evsSuppliers.get(1).get());
            assertSame(life, builder.evsSuppliers.get(2).get());
        }

        @Test
        @DisplayName("Builder stage selectors override default routing")
        void builderSelectorsOverrideDefaults()
        {
            ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
            ContextBuilder contextBuilder = configBuilder.context("/app");
            Context context = new Context("/app");
            contextBuilder.contextFactory(() -> context);

            // @formatter:off
            contextBuilder
                .begin()
                    .effect(String.class)
                        .create(v -> {}, "precondition effect")
                .immediate()
                    .invariant(String.class)
                        .create(v -> true, "immediate invariant")
                .validation()
                    .intent(String.class)
                        .read();
            contextBuilder.buildContext();
            // @formatter:on

            assertEquals(1, context.stage().at(StageName.BEGIN).size());
            assertEquals(1, context.stage().at(StageName.IMMEDIATE).size());
            assertEquals(1, context.stage().at(StageName.VALIDATION).size());
        }

        @Test
        @DisplayName("Builder supports transaction-oriented stage selectors")
        void builderSupportsTransactionOrientedSelectors()
        {
            ConfigBuilder configBuilder = new ConfigBuilder(new Existential());
            ContextBuilder contextBuilder = configBuilder.context("/app");
            Context context = new Context("/app");
            contextBuilder.contextFactory(() -> context);

            // @formatter:off
            contextBuilder
                .commit()
                    .effect(String.class)
                        .create(v -> {}, "commit effect")
                .checkpoint()
                    .effect(String.class)
                        .create(v -> {}, "checkpoint effect")
                .rollback()
                    .effect(String.class)
                        .create(v -> {}, "rollback effect");
            contextBuilder.buildContext();
            // @formatter:on

            assertEquals(1, context.stage().at(StageName.COMMIT).size());
            assertEquals(1, context.stage().at(StageName.CHECKPOINT).size());
            assertEquals(1, context.stage().at(StageName.ROLLBACK).size());
        }
    }
}
