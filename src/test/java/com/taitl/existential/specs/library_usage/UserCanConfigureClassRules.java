package com.taitl.existential.specs.library_usage;

import com.taitl.existential.specs.SpecBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
            ex.event(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure rules using fluent style")
    void configureRulesUsingFluentStyle()
    {
        assertDoesNotThrow(() -> {
            configureWithInstances();
            String tran = ex.begin(op).id();
            ex.event(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure rules using builders")
    void configureRulesUsingConfigBuilder()
    {
        assertDoesNotThrow(() -> {
            configure();
            String tran = ex.begin(op).id();
            ex.event(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure rules mixing fluent style and builders")
    void configureRulesMixingFluentAndBuilders()
    {
        assertDoesNotThrow(() -> {
            configureMixingFluentAndBuilders();
            String tran = ex.begin(op).id();
            ex.event(cat, tran);
        });
    }
}