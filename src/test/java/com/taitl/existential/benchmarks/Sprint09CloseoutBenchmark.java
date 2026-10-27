package com.taitl.existential.benchmarks;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.cross.caching.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Identical baseline/candidate public workflows plus an isolated shared metadata hit phase. */
class Sprint09CloseoutBenchmark
{
    static final String OP = "/city/sprint09-closeout";
    static volatile long consumed;

    @Test
    void workflows()
    {
        inference();
        for (String scenario : List.of("SUCCESS", "VALUES", "MEMO_ERROR"))
        {
            try (Fixture fixture = new Fixture(scenario))
            {
                measure(fixture, false);
                if (scenario.equals("MEMO_ERROR"))
                    measure(fixture, true);
            }
        }
    }

    void inference()
    {
        TypeKeyCache cache = Creator.singleton(TypeKeyCache.class);
        TypeKey<?> expected = cache.get(Cat.class, false);
        int attempts = 100000;
        for (int n = 0; n < 3; n++)
            for (int i = 0; i < attempts; i++)
                assertSame(expected, cache.get(Cat.class, false));
        long bytes = BenchmarkMemory.allocated();
        long started = System.nanoTime();
        for (int n = 0; n < 5; n++)
            for (int i = 0; i < attempts; i++)
                consumed += cache.get(Cat.class, false).toString().length();
        System.out.printf(Locale.ROOT, "CLASS-INFERENCE sample=%d attempts=%d ms=%.6f MB=%.6f%n",
                Integer.getInteger("benchmark.sample", 0), attempts, (System.nanoTime() - started) / 1_000_000d / 5,
                (BenchmarkMemory.allocated() - bytes) / 1_000_000d / 5);
    }

    void measure(Fixture fixture, boolean render)
    {
        int warmups = Integer.getInteger("benchmark.warmup.iterations", 3);
        int iterations = Integer.getInteger("benchmark.measure.iterations", 5);
        for (int i = 0; i < warmups; i++)
            fixture.run(render);
        long bytes = BenchmarkMemory.allocated();
        long started = System.nanoTime();
        for (int i = 0; i < iterations; i++)
            fixture.run(render);
        System.out.printf(Locale.ROOT,
                "SPRINT09-CLOSEOUT sample=%d scenario=%s attempts=1000 render=%s ms=%.6f MB=%.6f checks=%d effects=%d registry=0 cleanup=true%n",
                Integer.getInteger("benchmark.sample", 0), fixture.scenario, render,
                (System.nanoTime() - started) / 1_000_000d / iterations,
                (BenchmarkMemory.allocated() - bytes) / 1_000_000d / iterations, fixture.checks, fixture.effects);
    }

    static class Fixture implements AutoCloseable
    {
        Existential ex = new Existential();
        String scenario;
        int checks;
        int effects;
        Cat cat = new Cat("Black", "Park");

        Fixture(String scenario)
        {
            this.scenario = scenario;
            long started = System.nanoTime();
            long bytes = BenchmarkMemory.allocated();
            ex.configure().context(OP).invariant(Cat.class).read(value -> {
                checks++;
                return "Black".equals(value.color);
            }, "Black cats pass");
            ex.configure().context(OP).invariant(Command.class).read(value -> {
                checks++;
                return value.actor.equals("Keeper") && value.destination.equals("Garden");
            }, "Commands carry authorization values");
            if (scenario.equals("MEMO_ERROR"))
                ex.configure()
                        .context(OP)
                        .immediate()
                        .effect(Cat.class)
                        .transit((before, after) -> effects++, "Use memo");
            Tr setup = ex.begin(OP);
            setup.close();
            System.out.printf(Locale.ROOT, "CLOSEOUT-SETUP sample=%d scenario=%s ms=%.6f MB=%.6f%n",
                    Integer.getInteger("benchmark.sample", 0), scenario, (System.nanoTime() - started) / 1_000_000d,
                    (BenchmarkMemory.allocated() - bytes) / 1_000_000d);
        }

        void run(boolean render)
        {
            checks = 0;
            effects = 0;
            if (scenario.equals("MEMO_ERROR"))
            {
                Tr rejected = ex.begin(OP);
                try
                {
                    for (int i = 0; i < 1000; i++)
                    {
                        MemoException failure = assertThrows(MemoException.class, () -> rejected.update(cat));
                        assertEquals(MemoException.class, failure.getClass());
                        if (render)
                        {
                            consumed += failure.getMessage().length();
                            consumed += failure.getMessage().length();
                        }
                    }
                }
                finally
                {
                    rejected.close();
                }
                assertFalse(rejected.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
                Tr fresh = ex.begin(OP);
                try
                {
                    fresh.memo(new Cat("Black", "Park"), cat, Cat.class);
                    fresh.update(cat);
                    fresh.read(cat);
                    fresh.commit();
                }
                finally
                {
                    fresh.close();
                }
                assertFalse(fresh.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
                assertEquals(1, checks);
                assertEquals(1, effects);
            }
            else
            {
                for (int i = 0; i < 1000; i++)
                {
                    Tr tr = ex.begin(OP);
                    try
                    {
                        tr.read(cat);
                        if (scenario.equals("VALUES"))
                        {
                            Command command = new Command(cat, "Keeper", "Garden");
                            tr.read(command);
                            tr.read(command);
                        }
                        tr.commit();
                    }
                    finally
                    {
                        tr.close();
                    }
                    assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
                }
                assertEquals(scenario.equals("VALUES") ? 2000 : 1000, checks);
                assertEquals(0, effects);
            }
            assertEquals(0, ex.transactions().logic().registry().size());
        }

        public void close()
        {
            ex.close();
        }
    }

    static class Command
    {
        Cat cat;
        String actor;
        String destination;

        Command(Cat cat, String actor, String destination)
        {
            this.cat = cat;
            this.actor = actor;
            this.destination = destination;
        }
    }
}
