package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Applications reuse rule selections without reusing entity validation results")
class UserCanReuseRuleSelections
{
    @Test
    void reusesSelectorsAcrossEntitiesAndRevalidatesMutatedValues() throws Exception
    {
        AtomicInteger checks = new AtomicInteger();
        List<String> locations = new ArrayList<>();
        try (Existential ex = new Existential())
        {
            ex.configure()
                    .context("/cats")
                    .invariant(Cat.class)
                    .create(cat -> {
                        checks.incrementAndGet();
                        locations.add(cat.location());
                        return cat.location != null;
                    }, "Cats have locations");
            Cat cat = new Cat("Black", "Park");
            {
                Tr tr = ex.begin("/cats");
                try
                {
                    tr.create(cat);
                    tr.checkpoint();
                    assertEquals(1, checks.get());
                    cat.location = new Location("Garden");
                    tr.commit();
                    assertEquals(2, checks.get());
                }
                finally
                {
                    tr.close();
                }
            }
            {
                Tr tr = ex.begin("/cats");
                try
                {
                    tr.create(new Cat("Black", "Park"));
                    tr.commit();
                }
                finally
                {
                    tr.close();
                }
            }
            assertEquals(3, checks.get());
            assertEquals(List.of("Park", "Garden", "Park"), locations);
        }
    }

    @Test
    void recomputesEvictedOperationsAndReleasesResidentCachesOnShutdown() throws Exception
    {
        AtomicInteger calls = new AtomicInteger();
        Existential ex = new Existential();
        ex.configure()
                .context("/")
                .invariant(Cat.class)
                .create(cat -> {
                    calls.incrementAndGet();
                    return true;
                }, "Count declared checks");
        Config config = null;
        ConfigurationIndexes retained = null;
        try
        {
            for (int i = 0; i <= OperationIndexes.CAPACITY; i++)
            {
                String op = "/cats/" + i;
                {
                    Tr tr = ex.begin(op);
                    try
                    {
                        tr.create(new Cat("Black", "Park"));
                        tr.commit();
                    }
                    finally
                    {
                        tr.close();
                    }
                }
                config = ex.configs().config(op);
                retained = config.indexes(op, StageName.VALIDATION);
            }
            assertNotNull(config);
            assertEquals(OperationIndexes.CAPACITY, config.indexes(StageName.VALIDATION).operations.size());
            {
                Tr tr = ex.begin("/cats/0");
                try
                {
                    tr.create(new Cat("Black", "Garden"));
                    tr.commit();
                }
                finally
                {
                    tr.close();
                }
            }
            assertEquals(OperationIndexes.CAPACITY + 2, calls.get());
            assertTrue(retained.eventField().cachedSelections() > 0);
        }
        finally
        {
            ex.close();
        }
        assertNotNull(retained);
        assertEquals(0, retained.eventField().cachedSelections());
        assertEquals(0, config.indexes(StageName.VALIDATION).operations.size());
    }

    @Test
    void checksEveryEntitiesMemoAndUsesItsOwnBeforeState() throws Exception
    {
        List<String> moves = new ArrayList<>();
        try (Existential ex = new Existential())
        {
            // @formatter:off
            ex.configure().context("/cats").immediate().effect(Cat.class)
                .transit((before, after) -> moves.add(before.location() + "->" + after.location()),
                        "Record each cat relocation");
            // @formatter:on
            Cat first = new Cat("Black", "Garden");
            Cat second = new Cat("Black", "Library");
            Tr tr = ex.begin("/cats");
            try
            {
                tr.memo(new Cat("Black", "Park"), first, Cat.class);
                tr.update(first);
                assertThrows(com.taitl.existential.exceptions.MemoException.class, () -> tr.update(second));
                tr.memo(new Cat("Black", "Roof"), second, Cat.class);
                tr.update(second);
                tr.commit();
            }
            finally
            {
                tr.close();
            }
            assertEquals(List.of("Park->Garden", "Roof->Library"), moves);
        }
    }

    @Test
    void keepsDistinctDeletedEntitiesWhenTheAfterEndpointIsAbsent() throws Exception
    {
        AtomicInteger calls = new AtomicInteger();
        try (Existential ex = new Existential())
        {
            ex.configure()
                    .context("/cats")
                    .invariant(Being.class)
                    .delete(being -> calls.incrementAndGet() > 0, "Check every deleted being");
            Cat first = new Cat("Black", "Park");
            Cat equal = new Cat("Black", "Park");
            Tr tr = ex.begin("/cats");
            try
            {
                tr.port(first, null);
                tr.port(first, null);
                tr.port(equal, null);
                assertEquals(2, tr.runtimeIndexes().encounteredUniqueEvents.stream().count());
                assertEquals(0, calls.get());
                tr.commit();
                assertEquals(2, calls.get());
            }
            finally
            {
                tr.close();
            }
        }
    }

}
