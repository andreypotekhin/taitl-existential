package com.taitl.ex.logic.configuration.indexes.actions;

import java.util.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.invariants.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IndexConfigTypeKeyTest
{
    @Test
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
                        TypeKey.valueOf(String.class))));
        assertTrue(
                config.indexes().configuredHandlers.contains(EventKey.valueOf(com.taitl.existential.events.Create.class,
                        stringTypeKey)));
        assertTrue(
                config.indexes().configuredHandlers.contains(EventKey.valueOf(com.taitl.existential.events.Create.class,
                        reflectionTypeKey)));
    }

    @Test
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
}
