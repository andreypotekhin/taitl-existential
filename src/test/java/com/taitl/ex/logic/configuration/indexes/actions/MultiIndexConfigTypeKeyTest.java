package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MultiIndexConfigTypeKeyTest
{
    private Config config;
    private Context context;

    @BeforeEach
    void setup()
    {
        config = new Config();
        context = new Context("/app");
        config.addContext(context);
    }

    void index(StageName stage)
    {
        config.indexes(stage).indexConfig("/app", config, stage);
    }

    @Nested
    class Handlers
    {
        @Test
        @DisplayName("Indexes handlers using typed event keys for class string and reflection type keys")
        void indexesTypedKeys()
        {
            Invariant<String> classKeyInvariant = new Invariant<>(String.class);
            classKeyInvariant.create(s -> true, "class type key");
            context.invariant(classKeyInvariant);

            TypeKey<String> stringTypeKey = new TypeKey<>("String");
            Effect<String> stringKeyEffect = new Effect<>(stringTypeKey);
            stringKeyEffect.create(s -> {
            }, "string type key");
            context.effect(stringKeyEffect);

            TypeKey<List<String>> reflectionTypeKey = new TypeKey<List<String>>() {
            };
            Effect<List<String>> reflectionKeyEffect = new Effect<>(reflectionTypeKey);
            reflectionKeyEffect.create(v -> {
            }, "reflection type key");
            context.effect(reflectionKeyEffect);

            index(StageName.VALIDATION);

            assertTrue(config.indexes(StageName.VALIDATION).configuredHandlers.contains(
                    EventKey.valueOf(com.taitl.existential.events.Create.class, TypeKey.valueOf(String.class, false))));
            assertTrue(config.indexes(StageName.VALIDATION).configuredHandlers.contains(
                    EventKey.valueOf(com.taitl.existential.events.Create.class, stringTypeKey)));
            assertTrue(config.indexes(StageName.VALIDATION).configuredHandlers.contains(
                    EventKey.valueOf(com.taitl.existential.events.Create.class, reflectionTypeKey)));
        }

        @Test
        @DisplayName("Indexes full names using reflection type key full name constructor")
        void indexesFullNames()
        {
            config.indexes(StageName.VALIDATION).useFullClassNames(true);
            TypeKey<List<String>> fullTypeKey = new TypeKey<List<String>>(true) {
            };
            Effect<List<String>> effect = new Effect<>(fullTypeKey);
            effect.create(v -> {
            }, "typed effect");
            context.effect(effect);

            index(StageName.VALIDATION);

            assertTrue(config.indexes(StageName.VALIDATION).configuredHandlers.contains(
                    EventKey.valueOfFull(com.taitl.existential.events.Create.class, fullTypeKey)));
        }
    }

    @Nested
    class Intents
    {
        @Test
        @DisplayName("Indexes intent event keys with simple event names when type keys use full names")
        void indexesSimpleEventNames()
        {
            config.indexes(StageName.VALIDATION).useFullClassNames(true);
            TypeKey<String> fullTypeKey = TypeKey.valueOf(String.class, true);
            Intent<String> intent = new Intent<>(fullTypeKey);
            intent.read();
            context.intent(intent);

            index(StageName.IMMEDIATE);

            assertTrue(config.indexes(StageName.IMMEDIATE).configuredIntents
                    .contains(EventKey.valueOf(Read.class, fullTypeKey)));
            assertFalse(
                    config.indexes(StageName.IMMEDIATE).configuredIntents
                            .contains(EventKey.valueOfFull(Read.class, fullTypeKey)));
        }
    }

    @Nested
    class Wildcards
    {
        @Test
        @DisplayName("Rejects wildcard op when indexing config")
        void rejectsWildcardOp()
        {
            Config wildcardConfig = new Config();
            Context wildcard = new Context("/api/*/create");
            Effect<String> effect = new Effect<>(String.class);
            effect.create(v -> {
            }, "typed effect");
            wildcard.effect(effect);
            wildcardConfig.addContext(wildcard);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> wildcardConfig
                    .indexes(StageName.VALIDATION)
                    .indexConfig("/api/*/create", wildcardConfig, StageName.VALIDATION));

            assertTrue(ex.getMessage().contains("Cannot index wildcard context"));
        }
    }
}
