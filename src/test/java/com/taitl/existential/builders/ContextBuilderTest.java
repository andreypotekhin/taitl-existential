package com.taitl.existential.builders;

import java.util.*;
import java.util.function.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.invariants.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContextBuilderTest
{
    @Test
    void buildAttachesContextToParent()
    {
        ConfigBuilder configBuilder = new ConfigBuilder("/app");
        ContextBuilder contextBuilder = configBuilder.context("/app");
        Context context = new Context("/app");
        contextBuilder.contextFactory(() -> context);
        contextBuilder.invariant(new Invariant<>());

        contextBuilder.build();

        assertEquals(1, configBuilder.contexts.size());
        assertSame(context, configBuilder.contexts.get(0));
    }

    @Test
    void preservesContextRuleOrder()
    {
        ConfigBuilder configBuilder = new ConfigBuilder("/app");
        ContextBuilder contextBuilder = new ContextBuilder(configBuilder, "/app");
        Context context = new Context("/app");
        contextBuilder.contextFactory(() -> context);

        Invariant<String> inv1 = new Invariant<>();
        inv1.on(s -> true, "inv1");
        contextBuilder.invariant(inv1);

        contextBuilder.invariant(String.class)
                .create(s -> true, "inv2")
                .done();

        Effect<String> eff1 = new Effect<>();
        eff1.on(s -> {
        }, "eff1");
        contextBuilder.effect(eff1);

        contextBuilder.effect(String.class)
                .create(s -> {
                }, "eff2")
                .done();

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

        Invariant<String> inv1 = new Invariant<>();
        inv1.on(s -> true, "inv1");
        transactionBuilder.invariant(inv1);

        transactionBuilder.begin((Transaction tr) -> {
        });

        transactionBuilder.invariant(String.class)
                .create(s -> true, "inv2")
                .doneTran();

        Effect<String> eff1 = new Effect<>();
        eff1.on(s -> {
        }, "eff1");
        transactionBuilder.effect(eff1);

        transactionBuilder.effect(String.class)
                .create(s -> {
                }, "eff2")
                .doneTran();

        List<Supplier<? extends Evs<?>>> suppliers = transactionBuilder.evsSuppliers;
        assertEquals(5, suppliers.size());
        assertSame(inv1, suppliers.get(0).get());
        assertTrue(suppliers.get(1).get() instanceof Trancycle);
        assertTrue(((Invariant<?>) suppliers.get(2).get()).list().get(0) instanceof OnCreate);
        assertSame(eff1, suppliers.get(3).get());
        assertTrue(((Effect<?>) suppliers.get(4).get()).list().get(0) instanceof OnCreate);
    }
}
