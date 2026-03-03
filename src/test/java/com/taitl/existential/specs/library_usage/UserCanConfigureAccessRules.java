package com.taitl.existential.specs.library_usage;

import com.taitl.existential.exceptions.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.specs.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanConfigureAccessRules extends SpecBase
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

    @Test
    @DisplayName("User can configure access rules for a class")
    void configureAccessRulesForClass() throws Exception
    {
        // @formatter:off
        ex.configure()
            .context(op)
                .intent(cat.getClass())
                    .read()
                    .write()
                    .done()
                .build();
        // @formatter:on

        String tran = ex.begin(op).id();
        ex.read(cat, tran);
        ex.write(cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("When read intent exists for one class, another class requires explicit read intent")
    void otherClassRequiresExplicitReadIntent() throws Exception
    {
        // @formatter:off
        ex.configure()
            .context(op)
                .intent(cat.getClass())
                    .read()
                    .done()
                .build();
        // @formatter:on

        String tran = ex.begin(op).id();
        IntentViolation ex = assertThrows(IntentViolation.class, () -> this.ex.read("guest", tran));
        assertTrue(ex.getMessage().contains("No intent is configured"));
        assertTrue(ex.getMessage().contains("/Troubleshooting.md#intent-violation"));
        this.ex.rollback(tran);
    }

    @Test
    @DisplayName("Intent predicates are validated immediately when access event is emitted")
    void intentPredicatesAreValidatedImmediately() throws Exception
    {
        AtomicInteger writes = new AtomicInteger();

        // @formatter:off
        ex.configure()
            .context(op)
                .intent(cat.getClass())
                    .write(c -> writes.getAndIncrement() == 0)
                    .done()
                .build();
        // @formatter:on

        String tran = ex.begin(op).id();
        ex.write(cat, tran);
        IntentViolation ex = assertThrows(IntentViolation.class, () -> this.ex.write(cat, tran));
        assertTrue(ex.getMessage().contains("Intent condition is not met"));
        this.ex.rollback(tran);
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
                    .read()
                    .done()
                .build();
        // @formatter:on

        String tran = ex.begin(op).id();
        ex.read(cat, tran);
        IntentViolation ex = assertThrows(IntentViolation.class, () -> this.ex.read("guest", tran));
        assertTrue(ex.getMessage().contains("No intent is configured"));
        this.ex.rollback(tran);
    }

    @Test
    @DisplayName("Intent can be assigned to validation stage")
    void intentCanBeAssignedToValidationStage() throws Exception
    {
        // @formatter:off
        ex.configure()
            .context(op)
                .validation()
                    .intent(cat.getClass())
                        .write()
                        .done()
                .build();
        // @formatter:on

        String tran = ex.begin(op).id();
        ex.write(cat, tran);
        assertDoesNotThrow(() -> ex.commit(tran));
    }

    @Test
    @DisplayName("Validation stage intent rejects unauthorized type at commit")
    void validationStageIntentRejectsUnauthorizedTypeAtCommit() throws Exception
    {
        // @formatter:off
        ex.configure()
            .context(op)
                .validation()
                    .intent(cat.getClass())
                        .write()
                        .done()
                .build();
        // @formatter:on

        String tran = ex.begin(op).id();
        ex.write("guest", tran);
        IntentViolation ex = assertThrows(IntentViolation.class, () -> this.ex.commit(tran));
        assertTrue(ex.getMessage().contains("No intent is configured"));
        this.ex.rollback(tran);
    }

    @Test
    @DisplayName("Precondition stage intent is evaluated once per event key")
    void preconditionIntentEvaluatedOncePerEventKey() throws Exception
    {
        AtomicInteger checks = new AtomicInteger();

        // @formatter:off
        ex.configure()
            .context(op)
                .precondition()
                    .intent(cat.getClass())
                        .write(c -> checks.incrementAndGet() == 1)
                        .done()
                .build();
        // @formatter:on

        String tran = ex.begin(op).id();
        ex.write(cat, tran);
        ex.write(cat, tran);
        ex.commit(tran);

        assertEquals(1, checks.get());
    }
}
