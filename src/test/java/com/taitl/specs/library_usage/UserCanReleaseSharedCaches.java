package com.taitl.specs.library_usage;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.cross.caching.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/** Housekeeping.spec.md#cache-ownership-and-global-close; deterministic ownership, without forcing GC. */
class UserCanReleaseSharedCaches
{
    static final String OP = "/city/cache";

    @Test
    void sharedInferenceUsesClassAssociationsWithoutStrongClassKeyMaps() throws Exception
    {
        TypeKeyCache cache = Creator.singleton(TypeKeyCache.class);
        for (Field field : TypeKeyCache.class.getDeclaredFields())
        {
            assertTrue(ClassValue.class.isAssignableFrom(field.getType()), field.getName());
        }
        TypeKey<?> shortName = cache.get(Cat.class, false);
        TypeKey<?> fullName = cache.get(Cat.class, true);
        assertNotSame(shortName, fullName);
        assertSame(shortName, cache.get(new Cat("Black", "Park"), false));
        ExecutorService pool = Executors.newFixedThreadPool(4);
        try
        {
            List<Future<TypeKey<?>>> results = new ArrayList<>();
            for (int i = 0; i < 32; i++)
                results.add(pool.submit(() -> cache.get(Cat.class, true)));
            for (Future<TypeKey<?>> result : results)
                assertSame(fullName, result.get(5, TimeUnit.SECONDS));
        }
        finally
        {
            pool.shutdownNow();
        }
    }

    @Test
    void sameNamedClassesInDifferentLoadersHaveSeparateAssociations() throws Exception
    {
        String name = Cat.class.getName();
        byte[] bytes;
        try (java.io.InputStream in = Cat.class.getResourceAsStream("Cat.class"))
        {
            bytes = in.readAllBytes();
        }
        ClassLoader first = new ClassLoader(Cat.class.getClassLoader()) {
            protected Class<?> loadClass(String candidate, boolean resolve) throws ClassNotFoundException
            {
                return candidate.equals(name) ? defineClass(candidate, bytes, 0, bytes.length)
                        : super.loadClass(candidate, resolve);
            }
        };
        ClassLoader second = new ClassLoader(Cat.class.getClassLoader()) {
            protected Class<?> loadClass(String candidate, boolean resolve) throws ClassNotFoundException
            {
                return candidate.equals(name) ? defineClass(candidate, bytes, 0, bytes.length)
                        : super.loadClass(candidate, resolve);
            }
        };
        Class<?> one = first.loadClass(name);
        Class<?> two = second.loadClass(name);
        assertNotSame(one, two);
        TypeKeyCache cache = Creator.singleton(TypeKeyCache.class);
        assertNotSame(cache.get(one, true), cache.get(two, true));
        assertEquals(cache.get(one, true).toString(), cache.get(two, true).toString());
    }

    @Test
    void closingOneOwnerReleasesItsSelectionsAndKeepsAnotherOwnerUsable()
    {
        Existential first = configured();
        Existential second = configured();
        try
        {
            complete(first);
            complete(second);
            Config firstConfig = first.configs().config(OP);
            ConfigurationIndexes firstIndexes = firstConfig.indexes(OP, StageName.VALIDATION);
            ConfigurationIndexes secondIndexes = second.configs().config(OP).indexes(OP, StageName.VALIDATION);
            assertNotSame(firstIndexes, secondIndexes);
            assertTrue(firstIndexes.eventField().cachedSelections() > 0);
            TypeKey<?> shared = Creator.singleton(TypeKeyCache.class).get(Cat.class, false);
            first.close();
            assertEquals(0, firstIndexes.eventField().cachedSelections());
            assertEquals(0, firstConfig.indexes(StageName.VALIDATION).operations.size());
            assertTrue(secondIndexes.eventField().cachedSelections() > 0);
            complete(second);
            assertSame(shared, Creator.singleton(TypeKeyCache.class).get(Cat.class, false));
        }
        finally
        {
            first.close();
            second.close();
        }
    }

    @Test
    void facadeCloseIsTerminalIdempotentAndDoesNotCloseAnIndependentInstance()
    {
        Existential facade = configured();
        Existential independent = configured();
        Existential previous = Ex.instance(facade);
        try
        {
            Tr tr = Ex.begin(OP);
            try
            {
                Ex.read(new Cat("Black", "Park"), tr.id());
                Ex.commit(tr);
            }
            finally
            {
                tr.close();
            }
            Ex.close();
            Ex.close();
            assertSame(facade, Ex.instance());
            assertThrows(IllegalStateException.class, () -> Ex.begin(OP));
            complete(independent);
            Existential replacement = configured();
            assertSame(facade, Ex.instance(replacement));
            try
            {
                complete(Ex.instance());
            }
            finally
            {
                replacement.close();
            }
        }
        finally
        {
            Ex.instance(previous);
            facade.close();
            independent.close();
        }
    }

    Existential configured()
    {
        Existential ex = new Existential();
        ex.configure().context(OP).invariant(Cat.class).read(cat -> "Black".equals(cat.color), "Black cats pass");
        return ex;
    }

    void complete(Existential ex)
    {
        Tr tr = ex.begin(OP);
        try
        {
            tr.read(new Cat("Black", "Park"));
            tr.commit();
        }
        finally
        {
            tr.close();
        }
    }
}
