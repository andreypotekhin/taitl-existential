package com.taitl.existential.specs.library_usage;

import com.taitl.existential.keys.*;
import com.taitl.existential.specs.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanConfigureClassRules extends SpecBase
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

    void configureWithInstances()
    {
        fixt.configureWithInstances();
    }

    void configureMixingFluentAndBuilders()
    {
        fixt.configureMixingFluentAndBuilders();
    }

    @Test
    @DisplayName("User can configure class rules")
    void configureRules()
    {
        assertDoesNotThrow(() -> {
            configure();
            String tran = ex.begin(op).id();
            ex.update(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure rules using fluent style")
    void configureRulesUsingFluentStyle()
    {
        assertDoesNotThrow(() -> {
            configureWithInstances();
            String tran = ex.begin(op).id();
            ex.update(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure rules using builders")
    void configureRulesUsingConfigBuilder()
    {
        assertDoesNotThrow(() -> {
            configure();
            String tran = ex.begin(op).id();
            ex.update(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure rules mixing fluent style and builders")
    void configureRulesMixingFluentAndBuilders()
    {
        assertDoesNotThrow(() -> {
            configureMixingFluentAndBuilders();
            String tran = ex.begin(op).id();
            ex.update(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure class rules with a TypeKey for generic type")
    void configureRulesWithTypeKeyForGenericType()
    {
        AtomicInteger effectCalls = new AtomicInteger();
        AtomicInteger invariantChecks = new AtomicInteger();
        TypeKey<List<String>> typeKey = new TypeKey<List<String>>() {
        };

        assertDoesNotThrow(() -> {
            ex.configure()
                    .context(op)
                    .invariant(typeKey)
                    .create(v -> {
                        invariantChecks.incrementAndGet();
                        return !v.isEmpty();
                    }, "require at least one value")
                    .done()
                    .effect(typeKey)
                    .create(v -> effectCalls.incrementAndGet(), "track list creates")
                    .done()
                    .build();
            String tran = ex.begin(op).id();
            List<String> values = new ArrayList<>(List.of("ok"));
            ex.port(null, values, typeKey, tran);
            ex.commit(tran);
        });

        assertEquals(1, effectCalls.get());
        assertEquals(1, invariantChecks.get());
    }

    @Test
    @DisplayName("Execution stages evaluate precondition once and immediate on each event")
    void executionStagesEvaluatePreconditionOnceAndImmediateOnEachEvent() throws Exception
    {
        AtomicInteger preconditionCalls = new AtomicInteger();
        AtomicInteger immediateCalls = new AtomicInteger();
        AtomicInteger validationCalls = new AtomicInteger();

        // @formatter:off
        ex.configure()
            .context(op)
                .precondition()
                    .effect(cat.getClass())
                        .create(c -> preconditionCalls.incrementAndGet(), "precondition create")
                        .done()
                .immediate()
                    .effect(cat.getClass())
                        .create(c -> immediateCalls.incrementAndGet(), "immediate create")
                        .done()
                .validation()
                    .effect(cat.getClass())
                        .create(c -> validationCalls.incrementAndGet(), "validation create")
                        .done()
                .build();
        // @formatter:on

        String tran = ex.begin(op).id();
        ex.create(cat, tran);
        ex.create(cat, tran);
        ex.commit(tran);

        assertEquals(1, preconditionCalls.get());
        assertEquals(2, immediateCalls.get());
        assertEquals(1, validationCalls.get());
    }
}
