package com.taitl.existential.builders;

import com.taitl.existential.configs.*;
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

    @Test
    @DisplayName("Build attaches context to parent")
    void buildAttachesContextToParent()
    {
        ConfigBuilder configBuilder = new ConfigBuilder("/app");
        ContextBuilder contextBuilder = configBuilder.context();
        Context context = new Context("/app");
        contextBuilder.contextFactory(() -> context);
        contextBuilder.invariant(new Invariant<>(String.class));

        contextBuilder.build();

        assertEquals(1, configBuilder.contexts.size());
        assertSame(context, configBuilder.contexts.get(0));
    }

    @Test
    @DisplayName("Parameterless sibling context delegates to parent config op")
    void parameterlessSiblingContextDelegatesToParentConfigOp()
    {
        ConfigBuilder configBuilder = new ConfigBuilder("/app");
        ContextBuilder contextBuilder = new ContextBuilder(configBuilder, "/app");

        ContextBuilder sibling = contextBuilder.context();

        assertEquals("/app", sibling.op);
    }

    @Test
    @DisplayName("Preserves context rule order")
    void preservesContextRuleOrder()
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

        List<Evs<?>> evs = context.evs();
        assertEquals(6, evs.size());
        assertSame(inv1, evs.get(0));
        assertTrue(((Invariant<?>) evs.get(1)).list().get(0) instanceof OnCreate);
        assertSame(eff1, evs.get(2));
        assertSame(intent1, evs.get(3));
        assertTrue(((Effect<?>) evs.get(4)).list().get(0) instanceof OnCreate);
        assertTrue(((Intent<?>) evs.get(5)).list()
                .get(0) instanceof com.taitl.existential.handlers.access_handlers.OnWrite);
    }

    @Test
    @DisplayName("Preserves transaction rule order")
    void preservesTransactionRuleOrder()
    {
        ConfigBuilder configBuilder = new ConfigBuilder("/app");
        ContextBuilder contextBuilder = new ContextBuilder(configBuilder, "/app");
        TransactionBuilder transactionBuilder = contextBuilder.transaction(() -> new Transaction("/app", "test"));

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

    @Test
    @DisplayName("Context builder attaches type keys from class and type key overloads")
    void contextBuilderAttachesTypeKeysFromClassAndTypeKeyOverloads()
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

        List<Evs<?>> evs = context.evs();
        assertEquals(TypeKey.valueOf(String.class, false), evs.get(0).typeKey());
        assertEquals(reflectionType, evs.get(1).typeKey());
        assertEquals(stringType, evs.get(2).typeKey());
        assertEquals(TypeKey.valueOf(String.class, false), evs.get(3).typeKey());
    }

    @Test
    @DisplayName("Transaction lifecycle overloads assign type key to life")
    void transactionLifecycleOverloadsAssignTypeKeyToLife()
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

    @Test
    @DisplayName("Evs type key contract is never null")
    void evsTypeKeyContractIsNeverNull()
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
