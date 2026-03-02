package com.taitl.existential.builders;

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
                ConfigBuilder configBuilder = new ConfigBuilder("/app");
                ContextBuilder contextBuilder = configBuilder.context();
                com.taitl.existential.configs.Context context = new com.taitl.existential.configs.Context("/app");
                contextBuilder.contextFactory(() -> context);
                contextBuilder.invariant(new Invariant<>(String.class));

                contextBuilder.build();

                assertEquals(1, configBuilder.contexts.size());
                assertSame(context, configBuilder.contexts.get(0));
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
                ConfigBuilder configBuilder = new ConfigBuilder("/app");
                ContextBuilder contextBuilder = new ContextBuilder(configBuilder, "/app");

                ContextBuilder sibling = contextBuilder.context();

                assertEquals("/app", sibling.op);
            }
        }

        @Nested
        class RuleOrder
        {
            @Test
            @DisplayName("Preserves context rule order")
            void preserves()
            {
                ConfigBuilder configBuilder = new ConfigBuilder("/app");
                ContextBuilder contextBuilder = new ContextBuilder(configBuilder, "/app");
                Context context = new Context("/app");
                contextBuilder.contextFactory(() -> context);

                Invariant<String> inv1 = new Invariant<>(String.class);
                inv1.on(s -> true, "inv1");
                contextBuilder.invariant(inv1);

                // @formatter:off
                contextBuilder.invariant(String.class)
                    .create(s -> true, "inv2")
                    .done();
                // @formatter:on

                Effect<String> eff1 = new Effect<>(String.class);
                eff1.on(s -> {
                }, "eff1");
                contextBuilder.effect(eff1);

                Intent<String> intent1 = new Intent<>(String.class);
                intent1.read();
                contextBuilder.intent(intent1);

                // @formatter:off
                contextBuilder.effect(String.class)
                    .create(s -> {
                    }, "eff2")
                    .done();
                contextBuilder.intent(String.class)
                    .write()
                    .done();
                // @formatter:on

                contextBuilder.build();

                List<Evs<?>> precondition = context.stage().at(StageName.PRECONDITION);
                List<Evs<?>> immediate = context.stage().at(StageName.IMMEDIATE);
                List<Evs<?>> validation = context.stage().at(StageName.VALIDATION);

                assertEquals(0, precondition.size());
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
                ConfigBuilder configBuilder = new ConfigBuilder("/app");
                ContextBuilder contextBuilder = new ContextBuilder(configBuilder, "/app");
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
                transactionBuilder.intent(intent1);

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
                ConfigBuilder configBuilder = new ConfigBuilder("/app");
                ContextBuilder contextBuilder = new ContextBuilder(configBuilder, "/app");
                Context context = new Context("/app");
                contextBuilder.contextFactory(() -> context);
                TypeKey<List<String>> reflectionType = new TypeKey<List<String>>() {
                };
                TypeKey<String> stringType = new TypeKey<>("String");

                contextBuilder.invariant(String.class).create(s -> true, "inv").done();
                contextBuilder.invariant(reflectionType).create(v -> true, "list inv").done();
                contextBuilder.effect(stringType).create(s -> {
                }, "eff").done();
                contextBuilder.intent(String.class).read().done();

                contextBuilder.build();

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
                ConfigBuilder configBuilder = new ConfigBuilder("/app");
                ContextBuilder contextBuilder = new ContextBuilder(configBuilder, "/app");
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

            assertEquals(0, context.stage().at(StageName.PRECONDITION).size());
            assertEquals(1, context.stage().at(StageName.IMMEDIATE).size());
            assertEquals(2, context.stage().at(StageName.VALIDATION).size());
        }

        @Test
        @DisplayName("Transaction defaults route lifecycle rules to precondition stage")
        void transactionDefaults()
        {
            Transaction transaction = new Transaction("/app", "tx");
            transaction.invariant(new Invariant<>(String.class));
            transaction.effect(new Effect<>(String.class));
            transaction.intent(new Intent<>(String.class));
            transaction.cycle(new Life<>(Transaction.class));

            assertEquals(1, transaction.stage().at(StageName.PRECONDITION).size());
            assertEquals(1, transaction.stage().at(StageName.IMMEDIATE).size());
            assertEquals(2, transaction.stage().at(StageName.VALIDATION).size());
        }

        @Test
        @DisplayName("Builder stage selectors override default routing")
        void builderSelectorsOverrideDefaults()
        {
            ConfigBuilder configBuilder = new ConfigBuilder("/app");
            ContextBuilder contextBuilder = new ContextBuilder(configBuilder, "/app");
            Context context = new Context("/app");
            contextBuilder.contextFactory(() -> context);

            // @formatter:off
            contextBuilder
                .precondition()
                    .effect(String.class)
                        .create(v -> {
                        }, "precondition effect")
                    .done()
                .immediate()
                    .invariant(String.class)
                        .create(v -> true, "immediate invariant")
                    .done()
                .validation()
                    .intent(String.class)
                        .read()
                    .done()
                .build();
            // @formatter:on

            assertEquals(1, context.stage().at(StageName.PRECONDITION).size());
            assertEquals(1, context.stage().at(StageName.IMMEDIATE).size());
            assertEquals(1, context.stage().at(StageName.VALIDATION).size());
        }
    }
}
