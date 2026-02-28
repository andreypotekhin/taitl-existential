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
            ex.change(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure rules using fluent style")
    void configureRulesUsingFluentStyle()
    {
        assertDoesNotThrow(() -> {
            configureWithInstances();
            String tran = ex.begin(op).id();
            ex.change(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure rules using builders")
    void configureRulesUsingConfigBuilder()
    {
        assertDoesNotThrow(() -> {
            configure();
            String tran = ex.begin(op).id();
            ex.change(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure rules mixing fluent style and builders")
    void configureRulesMixingFluentAndBuilders()
    {
        assertDoesNotThrow(() -> {
            configureMixingFluentAndBuilders();
            String tran = ex.begin(op).id();
            ex.change(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure class rules with a TypeKey for generic type")
    void configureRulesWithTypeKeyForGenericType()
    {
        AtomicInteger calls = new AtomicInteger();
        TypeKey<List<String>> typeKey = new TypeKey<List<String>>() {
        };

        assertDoesNotThrow(() -> {
            ex.configure(op)
                    .context()
                    .effect(typeKey)
                    .create(v -> calls.incrementAndGet(), "track list creates")
                    .done()
                    .build();
            String tran = ex.begin(op).id();
            List<String> values = new ArrayList<>();
            ex.transit(null, values, typeKey, tran);
            ex.commit(tran);
        });

        assertEquals(1, calls.get());
    }
}
