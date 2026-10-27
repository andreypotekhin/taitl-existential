package com.taitl.specs.library_usage;

import com.taitl.existential.transactions.Tr;
import com.taitl.existential.constants.*;
import com.taitl.existential.exceptions.*;
import com.taitl.specs.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanConfigureIntents extends SpecBase
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

    @Nested
    class Scenarios
    {
        @Test
        @DisplayName("User can configure access rules for a class")
        void configureAccessRulesForClass() throws Exception
        {
            // @formatter:off
            ex.configure()
                .context(op)
                    .intent(cat.getClass())
                        .read()
                        .write();
            // @formatter:on

            Tr tranTr = ex.begin(op);
            try
            {
                String tran = tranTr.id();
                ex.read(cat, tran);
                ex.write(cat, tran);
                tranTr.commit();
            }
            finally
            {
                tranTr.close();
            }
        }

        @Test
        @DisplayName("When read intent exists for one class, another class requires explicit read intent")
        void otherClassRequiresExplicitReadIntent() throws Exception
        {
            // @formatter:off
            ex.configure()
                .context(op)
                    .intent(cat.getClass())
                        .read();
            // @formatter:on

            Tr tranTr = ex.begin(op);
            try
            {
                String tran = tranTr.id();
                IntentViolation ex = assertThrows(IntentViolation.class,
                        () -> UserCanConfigureIntents.this.ex.read("guest", tran));
                assertEquals(com.taitl.existential.diagnostics.DiagnosticCode.INTENT_MISSING, ex.diagnostic().code());
                assertTrue(ex.getMessage().contains("/Troubleshooting.md#diagnostic-intent-missing"));
                tranTr.rollback();
            }
            finally
            {
                tranTr.close();
            }
        }

        @Test
        @DisplayName("Intent predicates are validated immediately when access event is emitted")
        void intentPredicatesValidatedImmediately() throws Exception
        {
            AtomicInteger writes = new AtomicInteger();

            // @formatter:off
            ex.configure()
                .context(op)
                    .intent(cat.getClass())
                        .write(c -> writes.getAndIncrement() == 0);
            // @formatter:on

            Tr tranTr = ex.begin(op);
            try
            {
                String tran = tranTr.id();
                ex.write(cat, tran);
                IntentViolation ex = assertThrows(IntentViolation.class,
                        () -> UserCanConfigureIntents.this.ex.write(cat, tran));
                assertEquals(com.taitl.existential.diagnostics.DiagnosticCode.INTENT_REJECTED, ex.diagnostic().code());
                tranTr.rollback();
            }
            finally
            {
                tranTr.close();
            }
        }

        @Test
        @DisplayName("Intent evaluation supports full class names for business type keys")
        void intentEvaluationSupportsFullClassNames() throws Exception
        {
            ex.on(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);

            // @formatter:off
            ex.configure()
                .context(op)
                    .intent(cat.getClass())
                        .read();
            // @formatter:on

            Tr tranTr = ex.begin(op);
            try
            {
                String tran = tranTr.id();
                ex.read(cat, tran);
                IntentViolation ex = assertThrows(IntentViolation.class,
                        () -> UserCanConfigureIntents.this.ex.read("guest", tran));
                assertEquals(com.taitl.existential.diagnostics.DiagnosticCode.INTENT_MISSING, ex.diagnostic().code());
                tranTr.rollback();
            }
            finally
            {
                tranTr.close();
            }
        }

        @Test
        @DisplayName("Intent can be assigned to validation stage")
        void intentAtValidationStage() throws Exception
        {
            // @formatter:off
            ex.configure()
                .context(op)
                    .validation()
                        .intent(cat.getClass())
                            .write();
            // @formatter:on

            Tr tranTr = ex.begin(op);
            try
            {
                String tran = tranTr.id();
                ex.write(cat, tran);
                assertDoesNotThrow(() -> tranTr.commit());
            }
            finally
            {
                tranTr.close();
            }
        }

        @Test
        @DisplayName("Validation stage intent rejects unauthorized type at commit")
        void validationStageRejectsUnauthorizedType() throws Exception
        {
            // @formatter:off
            ex.configure()
                .context(op)
                    .validation()
                        .intent(cat.getClass())
                            .write();
            // @formatter:on

            Tr tranTr = ex.begin(op);
            try
            {
                String tran = tranTr.id();
                ex.write("guest", tran);
                IntentViolation ex = assertThrows(IntentViolation.class,
                        () -> tranTr.commit());
                assertEquals(com.taitl.existential.diagnostics.DiagnosticCode.INTENT_MISSING, ex.diagnostic().code());
                tranTr.rollback();
            }
            finally
            {
                tranTr.close();
            }
        }

        @Test
        @DisplayName("Begin stage intent is evaluated once per event key")
        void beginIntentOncePerEventKey() throws Exception
        {
            AtomicInteger checks = new AtomicInteger();

            // @formatter:off
            ex.configure()
                .context(op)
                    .begin()
                        .intent(cat.getClass())
                            .write(c -> checks.incrementAndGet() == 1);
            // @formatter:on

            Tr tranTr = ex.begin(op);
            try
            {
                String tran = tranTr.id();
                ex.write(cat, tran);
                ex.write(cat, tran);
                tranTr.commit();

                assertEquals(1, checks.get());
            }
            finally
            {
                tranTr.close();
            }
        }
    }
}
