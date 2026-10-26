package com.taitl.specs.configuration_workflow.custom_transactions;

import com.taitl.existential.transactions.Tr;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.specs.*;
import com.taitl.ex.examples.night_city.model.*;
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

    static class RequestTransaction extends Transaction
    {
        protected final String requiredColor;

        RequestTransaction(String op, String requiredColor)
        {
            super(op, "request-transaction");
            this.requiredColor = requiredColor;
        }

        RequestTransaction requireColorOnUpdate()
        {
            Invariant<Cat> invariant = new Invariant<>(Cat.class);
            invariant.update(cat -> requiredColor.equals(cat.color()), "Cat color must match request scope");
            invariant(invariant);
            return this;
        }

        RequestTransaction countUpdates(AtomicInteger updates)
        {
            Effect<Cat> effect = new Effect<>(Cat.class);
            effect.update(cat -> updates.incrementAndGet());
            effect(effect);
            return this;
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
            AtomicInteger configuredUpdates = new AtomicInteger();

            // @formatter:off
            Ex.configure()
                .context(op)
                    .transaction(() -> {
                        Transaction transaction = new Transaction(op, "configured-transaction");
                        Effect<Cat> effect = new Effect<>(Cat.class);
                        effect.update(cat -> configuredUpdates.incrementAndGet());
                        transaction.effect(effect);
                        return transaction;
                    })
                    ;
            // @formatter:on

            assertDoesNotThrow(() -> {
                Tr tranTr = ex.begin(op);
                String tran = tranTr.id();
                ex.update(cat, tran);
                tranTr.commit();
            });

            assertEquals(1, configuredUpdates.get());
        }

        @Test
        @DisplayName("Custom transaction instance")
        void customTransactionInstance() throws Exception
        {
            RequestTransaction custom = new RequestTransaction(op, "White").requireColorOnUpdate();

            configure();
            Tr tranTr = ex.begin(op, custom);
            String tran = tranTr.id();
            ex.update(cat, tran);

            ValidationStageExceptions thrown = assertThrows(ValidationStageExceptions.class,
                    () -> tranTr.commit());
            assertTrue(thrown.getCause().getMessage().contains("Cat color must match request scope"));
        }

        @Test
        @DisplayName("Custom transaction instance - request-scoped effects")
        void customTransactionInstanceCarriesEffects()
        {
            AtomicInteger updates = new AtomicInteger();
            RequestTransaction custom = new RequestTransaction(op, cat.color()).countUpdates(updates);

            configure();
            assertDoesNotThrow(() -> {
                Tr tranTr = ex.begin(op, custom);
                String tran = tranTr.id();
                ex.update(cat, tran);
                tranTr.commit();
            });

            assertEquals(1, updates.get());
        }

        @Test
        @DisplayName("Runtime custom rules stay isolated and are released with their handle")
        void runtimeCustomRulesStayIsolatedAndAreReleased()
        {
            configure();
            RequestTransaction black = new RequestTransaction(op, "Black").requireColorOnUpdate();
            Tr accepted = ex.begin(op, black);
            ex.update(cat, accepted.id());
            assertDoesNotThrow(accepted::commit);

            RequestTransaction white = new RequestTransaction(op, "White").requireColorOnUpdate();
            Tr rejected = ex.begin(op, white);
            ex.update(cat, rejected.id());
            assertThrows(ValidationStageExceptions.class, rejected::commit);
            rejected.rollback();

            assertNull(rejected.transactions(), "disposing the handle releases its transaction-local rules");
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
                Tr tranTr = ex.begin("/api/cats/create");
                String tran = tranTr.id();
                ex.update(cat, tran);
                tranTr.commit();
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
                Tr tranTr = ex.begin("/api/cats/create");
                String tran = tranTr.id();
                ex.update(cat, tran);
                tranTr.commit();
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
                Tr tranTr = ex.begin("/api/cats/create");
                String tran = tranTr.id();
                ex.update(cat, tran);
                tranTr.commit();
            });

            assertEquals(1, childBeginCalls.get());
        }
    }
}
