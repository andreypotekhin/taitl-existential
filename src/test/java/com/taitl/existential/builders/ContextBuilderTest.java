package com.taitl.existential.builders;

import java.util.*;
import java.util.function.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.invariants.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.Test;

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
    void parameterlessSiblingContextDelegatesToParentConfigOp()
    {
        ConfigBuilder configBuilder = new ConfigBuilder("/app");
        ContextBuilder contextBuilder = new ContextBuilder(configBuilder, "/app");

        ContextBuilder sibling = contextBuilder.context();

        assertEquals("/app", sibling.op);
    }

    @Test
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

        // @formatter:off
        contextBuilder.effect(String.class)
            .create(s -> {
            }, "eff2")
            .done();
        // @formatter:on

        contextBuilder.build();

        List<Evs<?>> evs = context.evs();
        assertEquals(4, evs.size());
        assertSame(inv1, evs.get(0));
        assertTrue(((Invariant<?>) evs.get(1)).list().get(0) instanceof OnCreate);
        assertSame(eff1, evs.get(2));
        assertTrue(((Effect<?>) evs.get(3)).list().get(0) instanceof OnCreate);
    }

    @Test
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

        // @formatter:off
        transactionBuilder.effect(String.class)
            .create(s -> {
            }, "eff2")
            .doneTran();
        // @formatter:on

        List<Supplier<? extends Evs<?>>> suppliers = transactionBuilder.evsSuppliers;
        assertEquals(5, suppliers.size());
        assertSame(inv1, suppliers.get(0).get());
        assertTrue(suppliers.get(1).get() instanceof Trancycle);
        assertTrue(((Invariant<?>) suppliers.get(2).get()).list().get(0) instanceof OnCreate);
        assertSame(eff1, suppliers.get(3).get());
        assertTrue(((Effect<?>) suppliers.get(4).get()).list().get(0) instanceof OnCreate);
    }

    @Test
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

        contextBuilder.build();

        List<Evs<?>> evs = context.evs();
        assertEquals(TypeKey.valueOf(String.class), evs.get(0).typeKey());
        assertEquals(reflectionType, evs.get(1).typeKey());
        assertEquals(stringType, evs.get(2).typeKey());
    }

    @Test
    void transactionLifecycleOverloadsAssignTypeKeyToTrancycle()
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

        Trancycle<?> beginCycle = (Trancycle<?>) transactionBuilder.evsSuppliers.get(0).get();
        Trancycle<?> commitCycle = (Trancycle<?>) transactionBuilder.evsSuppliers.get(1).get();

        assertEquals(TypeKey.valueOf(CustomTransaction.class), beginCycle.typeKey());
        assertEquals(reflectionFullNameType, commitCycle.typeKey());
    }

    @Test
    void evsTypeKeyContractIsNeverNull()
    {
        Invariant<String> invariant = new Invariant<>(String.class);
        Effect<String> effect = new Effect<>(new TypeKey<String>() {
        });
        Trancycle<Transaction> trancycle = new Trancycle<>(Transaction.class);
        Invariant<List<String>> reflected = new Invariant<List<String>>() {
        };

        assertNotNull(invariant.typeKey());
        assertNotNull(effect.typeKey());
        assertNotNull(trancycle.typeKey());
        assertEquals(new TypeKey<>(String.class), invariant.typeKey());
        assertEquals(new TypeKey<String>() {
        }, effect.typeKey());
        assertEquals(new TypeKey<Transaction>(Transaction.class), trancycle.typeKey());
        assertEquals(new TypeKey<List<String>>() {
        }, reflected.typeKey());
    }
}
