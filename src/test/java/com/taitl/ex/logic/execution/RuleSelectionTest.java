package com.taitl.ex.logic.execution;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.evaluation.events.split_events.*;
import com.taitl.ex.logic.evaluation.events.split_events.data.*;
import com.taitl.ex.logic.execution.actions.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.ex.logic.types.data.*;
import com.taitl.ex.logic.types.*;
import com.taitl.existential.events.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class RuleSelectionTest
{
    ConfigurationIndexes indexes = new ConfigurationIndexes();
    CountingSplit split = new CountingSplit();
    TypeKey<Cat> type = new TypeKey<>(Cat.class);

    @BeforeEach
    void configure()
    {
        indexes.doneIndexing();
    }

    @AfterEach
    void close()
    {
        indexes.close();
    }

    RuntimeKey<Cat> create(Cat cat)
    {
        return new RuntimeKey<>(new Create<>(cat), type, cat);
    }

    @Test
    void warmsBeforeExpansionAndRetainsOnlyOrderedDeclarations() throws Exception
    {
        OnCreate<Cat> handler = new OnCreate<>(cat -> {
        }, "Count selected declaration");
        indexes.addHandler(new EventKey<>(Create.class, type), handler);
        Cat first = new Cat("Black", "Park");
        Cat second = new Cat("Black", "Garden");
        SplitResult<Cat> cold = split.call(create(first), indexes.eventField(), false, true, null);
        SplitResult<Cat> warm = split.call(create(second), indexes.eventField(), false, true, null);
        assertEquals(1, split.input.calls.get());
        assertSame(cold.evaluables(), warm.evaluables());
        assertEquals(List.of(handler), warm.evaluables());
        assertSame(second, ((Create<Cat>) warm.event()).t);
        assertThrows(UnsupportedOperationException.class, () -> warm.evaluables().clear());
    }

    @Test
    void retainsSeparateRegistrationsOfTheSameHandler() throws Exception
    {
        OnCreate<Cat> handler = new OnCreate<>(cat -> {
        }, "Count selected declaration");
        EventKey<Cat> key = new EventKey<>(Create.class, type);
        indexes.addHandler(key, handler);
        indexes.addHandler(key, handler);
        List<?> handlers = split.call(create(new Cat("Black", "Park")), indexes.eventField(), false, true, null)
                .evaluables();
        assertEquals(List.of(handler, handler), handlers);
    }

    @Test
    void rebindsAfterInventoryChanges() throws Exception
    {
        RuntimeKey<Cat> key = create(new Cat("Black", "Park"));
        assertTrue(split.call(key, indexes.eventField(), false, true, null).evaluables().isEmpty());
        OnCreate<Cat> handler = new OnCreate<>(cat -> {
        }, "Count selected declaration");
        indexes.addHandler(new EventKey<>(Create.class, type), handler);
        assertEquals(List.of(handler), split.call(key, indexes.eventField(), false, true, null).evaluables());
        assertEquals(2, split.input.calls.get());
    }

    @Test
    void boundsNegativeSelectionsAndRecomputesAfterEviction() throws Exception
    {
        Cat cat = new Cat("Black", "Park");
        for (int i = 0; i <= PreparedSelections.CAPACITY; i++)
        {
            RuntimeKey<Cat> key = new RuntimeKey<>(new Create<>(cat), TypeKey.valueOf("Unmatched" + i), cat);
            assertTrue(split.call(key, indexes.eventField(), false, true, null).evaluables().isEmpty());
        }
        assertEquals(PreparedSelections.CAPACITY, indexes.eventField().cachedSelections());
        RuntimeKey<Cat> first = new RuntimeKey<>(new Create<>(cat), TypeKey.valueOf("Unmatched0"), cat);
        split.call(first, indexes.eventField(), false, true, null);
        assertEquals(PreparedSelections.CAPACITY + 2, split.input.calls.get());
        indexes.close();
        assertEquals(0, indexes.eventField().cachedSelections());
    }

    @Test
    void doesNotPublishAFailedResolution() throws Exception
    {
        split.input.fail = true;
        RuntimeKey<Cat> key = create(new Cat("Black", "Park"));
        assertThrows(IllegalStateException.class,
                () -> split.call(key, indexes.eventField(), false, true, null));
        assertEquals(0, indexes.eventField().cachedSelections());
        split.input.fail = false;
        assertTrue(split.call(key, indexes.eventField(), false, true, null).evaluables().isEmpty());
        assertEquals(2, split.input.calls.get());
    }

    @Test
    void publishesOneImmutableSelectionDuringConcurrentFirstUse() throws Exception
    {
        indexes.addHandler(new EventKey<>(Create.class, type), new OnCreate<Cat>(cat -> {
        }, "Count selected declaration"));
        ExecutorService pool = Executors.newFixedThreadPool(8);
        try
        {
            List<Callable<List<?>>> tasks = new ArrayList<>();
            for (int i = 0; i < 32; i++)
            {
                tasks.add(() -> split.call(create(new Cat("Black", "Park")), indexes.eventField(), false, true, null)
                        .evaluables());
            }
            List<Future<List<?>>> results = pool.invokeAll(tasks);
            List<?> selection = results.get(0).get();
            for (Future<List<?>> result : results)
            {
                assertSame(selection, result.get());
                assertEquals(1, result.get().size());
            }
            assertEquals(1, indexes.eventField().cachedSelections());
        }
        finally
        {
            pool.shutdownNow();
            assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    @Test
    void separatesPortEndpointShapes() throws Exception
    {
        OnCreate<Cat> born = new OnCreate<>(cat -> {
        }, "Count selected declaration");
        OnDelete<Cat> removed = new OnDelete<>(cat -> {
        }, "Count selected declaration");
        indexes.addHandler(new EventKey<>(Create.class, type), born);
        indexes.addHandler(new EventKey<>(Delete.class, type), removed);
        Cat cat = new Cat("Black", "Park");
        RuntimeKey<Cat> create = new RuntimeKey<>(new Port<>(null, cat), type, cat);
        RuntimeKey<Cat> delete = new RuntimeKey<>(new Port<>(cat, null), type, cat);
        assertEquals(List.of(born), split.call(create, indexes.eventField(), false, true, null).evaluables());
        assertEquals(List.of(removed), split.call(delete, indexes.eventField(), false, true, null).evaluables());
        assertEquals(2, split.input.calls.get());
    }

    @Test
    void keepsOverlayGenerationAndOwnershipSeparate() throws Exception
    {
        ConfigurationIndexes local = new ConfigurationIndexes();
        local.doneIndexing();
        EventFieldOverlay overlay = new EventFieldOverlay(indexes.eventField(), local.eventField());
        try
        {
            RuntimeKey<Cat> key = create(new Cat("Black", "Park"));
            assertTrue(split.call(key, overlay, false, true, null).evaluables().isEmpty());
            OnCreate<Cat> handler = new OnCreate<>(cat -> {
            }, "Count selected declaration");
            local.addHandler(new EventKey<>(Create.class, type), handler);
            assertEquals(List.of(handler), split.call(key, overlay, false, true, null).evaluables());
            assertTrue(split.call(key, indexes.eventField(), false, true, null).evaluables().isEmpty());
        }
        finally
        {
            overlay.close();
            local.close();
        }
        assertEquals(0, overlay.cachedSelections());
        assertEquals(1, indexes.eventField().cachedSelections());
    }

    @Test
    void separatesNamingAndEventExpansionModes() throws Exception
    {
        OnCreate<Cat> handler = new OnCreate<>(cat -> {
        }, "Count declaration");
        indexes.addHandler(new EventKey<>(Create.class, type), handler);
        RuntimeKey<Cat> key = create(new Cat("Black", "Park"));
        assertEquals(List.of(handler), split.call(key, indexes.eventField(), false, true, null).evaluables());
        assertTrue(split.call(key, indexes.eventField(), true, true, null).evaluables().isEmpty());
        split.call(key, indexes.eventField(), false, false, null);
        assertEquals(3, split.input.calls.get());
    }

    @Test
    void matchesConfiguredWildcardsWithoutEnumeratingTheirProducts()
    {
        String arguments = String.join(",", Collections.nCopies(16, "Cat"));
        String wildcards = String.join(",", Collections.nCopies(16, "?"));
        RuntimeKey<Cat> key = new RuntimeKey<>(new Create<>(new Cat("Black", "Park")),
                TypeKey.valueOf("Box<" + arguments + ">"), new Cat("Black", "Park"));
        Set<TypePattern> types = split.input.types(key, false);
        assertTrue(types.size() < 16);
        TypePattern pattern =
                new TypePattern("Box<" + wildcards + ">");
        assertTrue(types.stream().anyMatch(reported -> new MatchTypePattern().call(pattern, reported)));
    }

    @Test
    void preservesRuntimeClassLoaderIdentity() throws Exception
    {
        String resource = "/" + Cat.class.getName().replace('.', '/') + ".class";
        byte[] bytecode;
        try (java.io.InputStream input = Cat.class.getResourceAsStream(resource))
        {
            assertNotNull(input);
            bytecode = input.readAllBytes();
        }
        Object first = new CatLoader().define(bytecode)
                .getConstructor(String.class, String.class)
                .newInstance("Black", "Park");
        Object second = new CatLoader().define(bytecode)
                .getConstructor(String.class, String.class)
                .newInstance("Black", "Garden");
        TypeKey<Object> declared = TypeKey.valueOf("Cat");
        RuntimeKey<Object> a = new RuntimeKey<>(new Create<>(first), declared, first);
        RuntimeKey<Object> b = new RuntimeKey<>(new Create<>(second), declared, second);
        assertNotEquals(new DispatchSignature(a, false, true, false, false),
                new DispatchSignature(b, false, true, false, false));
        split.call(a, indexes.eventField(), false, true, null);
        split.call(b, indexes.eventField(), false, true, null);
        assertEquals(2, split.input.calls.get());
    }

    static class CatLoader extends ClassLoader
    {
        Class<?> define(byte[] bytecode)
        {
            return defineClass(Cat.class.getName(), bytecode, 0, bytecode.length);
        }
    }

    static class CountingSplit extends SplitEvent
    {
        CountingInput input = new CountingInput();

        CountingSplit()
        {
            resolveSelectionInput = input;
        }
    }

    static class CountingInput extends ResolveSelectionInput
    {
        AtomicInteger calls = new AtomicInteger();
        boolean fail;

        public <T> SelectionInput call(RuntimeKey<T> key, boolean names, boolean split, Tr tr)
        {
            calls.incrementAndGet();
            if (fail)
            {
                throw new IllegalStateException("Selection metadata unavailable");
            }
            return super.call(key, names, split, tr);
        }
    }
}
