package com.taitl.existential.specs.configuration_workflow.custom_transactions;

import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.specs.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

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

    @Nested
    class Scenarios
    {
        @Test
        @DisplayName("Configuring transactions")
        void configuringTransactions()
        {
            assertDoesNotThrow(() -> {
                fixt.configureTransactionRules();
                String tran = ex.begin(op).id();
                ex.update(cat, tran);
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
                ex.update(cat, tran);
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
            Ex.configure()
                .context("/api/cats/create")
                    .transaction(() -> {
                        created.incrementAndGet();
                        return new ChildTransaction("/api/cats/create");
                    })
                        .begin((ChildTransaction tr) -> transactionType.set(tr.getClass().getSimpleName()))
                    ;
            // @formatter:on

            assertDoesNotThrow(() -> {
                String tran = ex.begin("/api/cats/create").id();
                ex.update(cat, tran);
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
            Ex.configure()
                .context("/api/cats")
                    .transaction(() -> new RootTransaction("/api/cats"))
                        .begin((RootTransaction tr) -> transactionTypes.add(tr.getClass().getSimpleName()))
                        .context("/api/cats/create")
                    .transaction(() -> new ChildTransaction("/api/cats/create"))
                        .begin((ChildTransaction tr) -> transactionTypes.add(tr.getClass().getSimpleName()))
                    ;
            // @formatter:on

            assertDoesNotThrow(() -> {
                String tran = ex.begin("/api/cats/create").id();
                ex.update(cat, tran);
                ex.commit(tran);
            });

            assertEquals(
                    List.of(RootTransaction.class.getSimpleName(), ChildTransaction.class.getSimpleName()),
                    transactionTypes);
        }

        @Test
        @DisplayName("User can configure transaction lifecycle side effects with TypeKey for custom transaction type")
        void lifecycleRulesWithTypeKey()
        {
            AtomicInteger childBeginCalls = new AtomicInteger();
            TypeKey<ChildTransaction> childTypeKey = new TypeKey<ChildTransaction>() {
            };

            // @formatter:off
            Ex.configure()
                .context("/api/cats/create")
                    .transaction(() -> new ChildTransaction("/api/cats/create"))
                        .begin(childTypeKey, tr -> childBeginCalls.incrementAndGet())
                    ;
            // @formatter:on

            assertDoesNotThrow(() -> {
                String tran = ex.begin("/api/cats/create").id();
                ex.update(cat, tran);
                ex.commit(tran);
            });

            assertEquals(1, childBeginCalls.get());
        }
    }
}
