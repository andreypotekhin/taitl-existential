package com.taitl.ex.logic.events.logic;

import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.*;
import com.taitl.ex.logic.types.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.io.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

class SplitTypeKeyTest
{
    private SplitTypeKey splitter;

    @BeforeEach
    void setup()
    {
        splitter = new SplitTypeKey();
    }

    @Nested
    class Root
    {
        @Test
        @DisplayName("Removes generic part and keeps only type name")
        void removesGenericPart()
        {
            assertEquals("Read", splitter.root("Read<Cat<JSON>>"));
        }

        @Test
        @DisplayName("Supports plain names and whitespace")
        void supportsWhitespace()
        {
            assertEquals("Write", splitter.root("  Write  "));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Split
    {
        private Set<String> expectedMulti;

        @BeforeAll
        void setupExpected()
        {
            expectedMulti = Set.of(
                    "T<A<X>,B<Y>>",
                    "T<?,B<Y>>",
                    "T<A<X>,?>",
                    "T<?,?>",
                    "T");
        }

        @Test
        @DisplayName("Single dimension")
        void single()
        {
            Set<String> keys = splitter.split(TypeKey.valueOf("T<A<X>>"))
                    .stream()
                    .map(TypeKey::toString)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            assertEquals(Set.of("T<A<X>>", "T<?>", "T"), keys);
        }

        @Test
        @DisplayName("Multiple dimensions")
        void multiple()
        {
            Set<String> keys = splitter.split(TypeKey.valueOf("T<A<X>,B<Y>>"))
                    .stream()
                    .map(TypeKey::toString)
                    .collect(Collectors.toSet());

            assertEquals(expectedMulti, keys);
        }
    }

    @Test
    void cachesResolvedDescriptorsWithinBound()
    {
        TypeDescriptorCache cache = new TypeDescriptorCache();
        TypeKey<Object> first = TypeKey.valueOf("Report<Cat>");
        Set<TypeKey<Object>> resolved = cache.resolve(first, String.class, false,
                () -> Set.of(TypeKey.valueOf("Report<Cat>")));

        assertSame(resolved, cache.resolve(first, String.class, false,
                () -> fail("Cached descriptor should be reused")));
        for (int i = 0; i <= TypeDescriptorCache.CAPACITY; i++)
        {
            String name = "Report<" + i + ">";
            TypeKey<Object> key = TypeKey.valueOf(name);
            cache.resolve(key, String.class, false, () -> Set.of(TypeKey.valueOf(name)));
        }

        assertEquals(TypeDescriptorCache.CAPACITY, cache.size());
    }

    @Test
    void distinguishesUnresolvedFromResolvedGenericSignatures()
    {
        TypeMatchingLogic matching = new TypeMatchingLogic();

        assertTrue(matching.hasUnresolvedGenerics(GenericReport.class));
        assertTrue(matching.hasUnresolvedGenerics(PartialReport.class));
        assertFalse(matching.hasUnresolvedGenerics(ResolvedReport.class));
    }

    @Test
    void resolvesConcurrentFirstUseAsOneCompleteSelection() throws Exception
    {
        TypeDescriptorCache cache = new TypeDescriptorCache();
        TypeKey<Object> typeKey = TypeKey.valueOf("Report<Cat>");
        AtomicInteger resolutions = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService workers = Executors.newFixedThreadPool(8);
        List<Future<Set<TypeKey<Object>>>> results = new ArrayList<>();
        try
        {
            for (int i = 0; i < 8; i++)
            {
                results.add(workers.submit(() -> {
                    start.await();
                    return cache.resolve(typeKey, String.class, false, () -> {
                        resolutions.incrementAndGet();
                        return Set.of(typeKey);
                    });
                }));
            }
            start.countDown();
            Set<TypeKey<Object>> first = results.get(0).get();
            for (Future<Set<TypeKey<Object>>> result : results)
            {
                assertSame(first, result.get());
                assertEquals(Set.of(typeKey), result.get());
            }
            assertEquals(1, resolutions.get());
        }
        finally
        {
            workers.shutdownNow();
        }
    }

    @Test
    void cacheDistinguishesSameNamedClassesFromDifferentClassLoaders() throws Exception
    {
        String name = GenericReport.class.getName();
        byte[] bytecode;
        String resource = name.replace('.', '/') + ".class";
        try (InputStream stream = GenericReport.class.getClassLoader().getResourceAsStream(resource))
        {
            assertNotNull(stream);
            bytecode = stream.readAllBytes();
        }
        Class<?> firstClass = new IsolatedClassLoader(name, bytecode).loadClass(name);
        Class<?> secondClass = new IsolatedClassLoader(name, bytecode).loadClass(name);
        TypeDescriptorCache cache = new TypeDescriptorCache();
        TypeKey<Object> typeKey = TypeKey.valueOf("Report<Cat>");
        AtomicInteger resolutions = new AtomicInteger();

        cache.resolve(typeKey, firstClass, false, () -> {
            resolutions.incrementAndGet();
            return Set.of(typeKey);
        });
        cache.resolve(typeKey, secondClass, false, () -> {
            resolutions.incrementAndGet();
            return Set.of(typeKey);
        });

        assertEquals(firstClass.getName(), secondClass.getName());
        assertNotSame(firstClass, secondClass);
        assertEquals(2, resolutions.get());
        assertEquals(2, cache.size());
    }

    @Test
    void cacheSeparatesNameModesAndRetainsEmptyResolution()
    {
        TypeDescriptorCache cache = new TypeDescriptorCache();
        TypeKey<Object> typeKey = TypeKey.valueOf("Report<Cat>");
        AtomicInteger resolutions = new AtomicInteger();
        cache.resolve(typeKey, String.class, false, () -> {
            resolutions.incrementAndGet();
            return Collections.emptySet();
        });
        Set<TypeKey<Object>> fullNames = cache.resolve(typeKey, String.class, true, () -> {
            resolutions.incrementAndGet();
            return Set.of(typeKey);
        });
        Set<TypeKey<Object>> empty = cache.resolve(typeKey, String.class, false,
                () -> fail("Empty descriptor result should be cached"));

        assertTrue(empty.isEmpty());
        assertEquals(Set.of(typeKey), fullNames);
        assertEquals(2, resolutions.get());
        assertEquals(2, cache.size());
    }

    private static class GenericReport<T>
    {
    }

    private static class PartialReport<T> extends GenericReport<List<T>>
    {
    }

    private static class ResolvedReport extends GenericReport<String>
    {
    }

    private static class IsolatedClassLoader extends ClassLoader
    {
        private final String className;
        private final byte[] bytecode;

        IsolatedClassLoader(String className, byte[] bytecode)
        {
            super(SplitTypeKeyTest.class.getClassLoader());
            this.className = className;
            this.bytecode = bytecode;
        }

        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
        {
            if (!className.equals(name))
            {
                return super.loadClass(name, resolve);
            }
            Class<?> loaded = findLoadedClass(name);
            if (loaded == null)
            {
                loaded = defineClass(name, bytecode, 0, bytecode.length);
            }
            if (resolve)
            {
                resolveClass(loaded);
            }
            return loaded;
        }
    }
}
