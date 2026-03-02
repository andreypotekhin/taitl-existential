package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class IndexConfigTypeKeyTest
{
    @Test
    @DisplayName("Indexes handlers using typed event keys for class string and reflection type keys")
    void indexesHandlersUsingTypedEventKeysForClassStringAndReflectionTypeKeys()
    {
        Config config = new Config();
        Context context = new Context("/app");

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

        config.addContext(context);
        config.indexes().indexConfig("/app", config);

        assertTrue(
                config.indexes().configuredHandlers.contains(EventKey.valueOf(com.taitl.existential.events.Create.class,
                        TypeKey.valueOf(String.class, false))));
        assertTrue(
                config.indexes().configuredHandlers.contains(EventKey.valueOf(com.taitl.existential.events.Create.class,
                        stringTypeKey)));
        assertTrue(
                config.indexes().configuredHandlers.contains(EventKey.valueOf(com.taitl.existential.events.Create.class,
                        reflectionTypeKey)));
    }

    @Test
    @DisplayName("Indexes full names using reflection type key full name constructor")
    void indexesFullNamesUsingReflectionTypeKeyFullNameConstructor()
    {
        Config config = new Config();
        config.indexes().useFullClassNames(true);
        Context context = new Context("/app");
        TypeKey<List<String>> fullTypeKey = new TypeKey<List<String>>(true) {
        };
        Effect<List<String>> effect = new Effect<>(fullTypeKey);
        effect.create(v -> {
        }, "typed effect");
        context.effect(effect);
        config.addContext(context);

        config.indexes().indexConfig("/app", config);

        assertTrue(config.indexes().configuredHandlers.contains(
                EventKey.valueOfFull(com.taitl.existential.events.Create.class, fullTypeKey)));
    }

    @Test
    @DisplayName("Indexes intent event keys with simple event names when type keys use full names")
    void indexesIntentEventKeysWithSimpleEventNamesWhenTypeKeysUseFullNames()
    {
        Config config = new Config();
        config.indexes().useFullClassNames(true);
        Context context = new Context("/app");
        TypeKey<String> fullTypeKey = TypeKey.valueOf(String.class, true);
        Intent<String> intent = new Intent<>(fullTypeKey);
        intent.read();
        context.intent(intent);
        config.addContext(context);

        config.indexes(StageName.IMMEDIATE).indexConfig("/app", config, StageName.IMMEDIATE);

        assertTrue(config.indexes(StageName.IMMEDIATE).configuredIntents
                .contains(EventKey.valueOf(Read.class, fullTypeKey)));
        assertFalse(
                config.indexes(StageName.IMMEDIATE).configuredIntents
                        .contains(EventKey.valueOfFull(Read.class, fullTypeKey)));
    }
}
