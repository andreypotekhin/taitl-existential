package com.taitl.existential.specs.configuration_workflow.custom_transactions;

import com.taitl.existential.Ex;
import com.taitl.existential.configs.Transaction;
import com.taitl.existential.specs.SpecBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserCanConfigureCustomTransactions extends SpecBase
{
    static class RootTransaction extends Transaction
    {
        RootTransaction(String op)
        {
            super(op, "root-transaction");
        }
    }

    static class ChildTransaction extends RootTransaction
    {
        ChildTransaction(String op)
        {
            super(op);
            name("child-transaction");
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
    @DisplayName("Configuring transactions")
    void configuringTransactions()
    {
        assertDoesNotThrow(() -> {
            fixt.configureTransactionRules();
            String tran = ex.begin(op).id();
            ex.event(cat, tran);
            ex.commit(tran);
        });
    }

    @Test
    @DisplayName("Custom transaction instance")
    void customTransactionInstance()
    {
        assertDoesNotThrow(() -> {
            fixt.configureTransactionRules();
            Transaction custom = new Transaction(op, "request-scope");
            String tran = ex.begin(op, custom).id();
            ex.event(cat, tran);
            ex.commit(tran);
        });
    }

    @Test
    @DisplayName("Configuring transactions - custom factory in context")
    void customFactoryInContext()
    {
        AtomicInteger created = new AtomicInteger();
        AtomicReference<String> transactionType = new AtomicReference<>();

        // @formatter:off
        Ex.configure("/api/cats/create")
            .context()
                .transaction(() -> {
                    created.incrementAndGet();
                    return new ChildTransaction("/api/cats/create");
                })
                .begin((ChildTransaction tr) -> transactionType.set(tr.getClass().getSimpleName()))
                .build()
            .build();
        // @formatter:on

        assertDoesNotThrow(() -> {
            String tran = ex.begin("/api/cats/create").id();
            ex.event(cat, tran);
            ex.commit(tran);
        });

        assertEquals(1, created.get());
        assertEquals(ChildTransaction.class.getSimpleName(), transactionType.get());
    }

    @Test
    @DisplayName("Configuring transactions - child context overrides inherited factory")
    void childContextOverridesInheritedFactory()
    {
        List<String> transactionTypes = new ArrayList<>();

        // @formatter:off
        Ex.configure("/api/cats")
            .context()
                .transaction(() -> new RootTransaction("/api/cats"))
                .begin((RootTransaction tr) -> transactionTypes.add(tr.getClass().getSimpleName()))
                .build()
            .context("/api/cats/create")
                .transaction(() -> new ChildTransaction("/api/cats/create"))
                .begin((ChildTransaction tr) -> transactionTypes.add(tr.getClass().getSimpleName()))
                .build()
            .build();
        // @formatter:on

        assertDoesNotThrow(() -> {
            String tran = ex.begin("/api/cats/create").id();
            ex.event(cat, tran);
            ex.commit(tran);
        });

        assertEquals(
                List.of(RootTransaction.class.getSimpleName(), ChildTransaction.class.getSimpleName()),
                transactionTypes);
    }
}
