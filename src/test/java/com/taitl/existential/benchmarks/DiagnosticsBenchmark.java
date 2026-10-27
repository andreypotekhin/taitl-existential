package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Uses only APIs shared by the dirty pre-diagnostics baseline and candidate. */
class DiagnosticsBenchmark
{
    static final String OP = "/city/diagnostics/benchmark";
    static volatile long consumed;

    @Test
    void completeWorkflows()
    {
        for (int entities : new int[] { 10000, 50000 })
        {
            for (int repeats : new int[] { 1, 8 })
            {
                for (int checks : new int[] { 1, 100 })
                {
                    try (Fixture fixture = new Fixture(entities, repeats, checks, "success"))
                    {
                        measure(fixture, false);
                    }
                }
                for (String scenario : List.of("rejection", "recovery"))
                {
                    try (Fixture fixture = new Fixture(entities, repeats, 1, scenario))
                    {
                        measure(fixture, false);
                        measure(fixture, true);
                    }
                }
            }
        }
        for (String scenario : List.of("collection", "effects"))
        {
            try (Fixture fixture = new Fixture(1000, 1, 3, scenario))
            {
                measure(fixture, false);
                measure(fixture, true);
            }
        }
    }

    void measure(Fixture fixture, boolean render)
    {
        int warmup = Integer.getInteger("benchmark.warmup.iterations", 3);
        int iterations = Integer.getInteger("benchmark.measure.iterations", 5);
        for (int i = 0; i < warmup; i++)
            fixture.run(render);
        fixture.firstRendering = 0;
        fixture.repeatedRendering = 0;
        long allocated = BenchmarkMemory.allocated();
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++)
            fixture.run(render);
        long elapsed = System.nanoTime() - start;
        allocated = BenchmarkMemory.allocated() - allocated;
        System.out.printf(Locale.ROOT,
                "DIAGNOSTICS sample=%d scenario=%s entities=%d checks=%d repeats=%d render=%s ms=%.6f MB=%.6f first-us=%.6f repeated-us=%.6f callbacks=%d violations=%d folded=%d cleanup=true%n",
                Integer.getInteger("benchmark.sample", 0), fixture.scenario, fixture.cats.size(), fixture.checks,
                fixture.repeats, render, elapsed / 1_000_000d / iterations, allocated / 1_000_000d / iterations,
                fixture.firstRendering / 1000d / iterations, fixture.repeatedRendering / 1000d / iterations,
                fixture.expectedChecks(), fixture.expectedViolations(), fixture.cats.size());
    }

    static class Fixture implements AutoCloseable
    {
        Existential ex = new Existential();
        List<Cat> cats = new ArrayList<>();
        int repeats;
        int checks;
        String scenario;
        long calls;
        long effects;
        long firstRendering;
        long repeatedRendering;

        Fixture(int entities, int repeats, int checks, String scenario)
        {
            long bytes = BenchmarkMemory.allocated();
            long start = System.nanoTime();
            this.repeats = repeats;
            this.checks = checks;
            this.scenario = scenario;
            for (int i = 0; i < entities; i++)
                cats.add(new Cat("Black", "Park"));
            if (scenario.equals("effects"))
                ex.configure().context(OP).effect(new Effect<>(Cat.class).read(cat -> effects++, "Preceding effect"));
            Invariant<Cat> rules = new Invariant<>(Cat.class);
            for (int i = 0; i < checks; i++)
                rules.read(cat -> {
                    calls++;
                    return !scenario.equals("collection") && !scenario.equals("effects") && cat.color.equals("Black");
                }, "Cat check " + i);
            ex.configure().context(OP).invariant(rules);
            Tr setup = ex.begin(OP);
            setup.close();
            System.out.printf(Locale.ROOT,
                    "SETUP sample=%d scenario=%s entities=%d checks=%d repeats=%d ms=%.6f MB=%.6f%n",
                    Integer.getInteger("benchmark.sample", 0), scenario, entities, checks, repeats,
                    (System.nanoTime() - start) / 1_000_000d, (BenchmarkMemory.allocated() - bytes) / 1_000_000d);
        }

        long expectedChecks()
        {
            return scenario.equals("effects") ? 1 : (long) cats.size() * checks * (scenario.equals("recovery") ? 2 : 1);
        }

        int expectedViolations()
        {
            return scenario.equals("success") ? 0 : scenario.equals("collection") ? cats.size() * checks : 1;
        }

        void run(boolean render)
        {
            calls = 0;
            effects = 0;
            workflow(!scenario.equals("success"), render);
            if (scenario.equals("recovery"))
                workflow(false, render);
            assertEquals(expectedChecks(), calls);
            assertEquals(scenario.equals("effects") ? 1 : 0, effects);
        }

        void workflow(boolean rejected, boolean render)
        {
            cats.get(cats.size() - 1).color = rejected ? "Grey" : "Black";
            Tr tr = ex.begin(OP);
            try
            {
                for (Cat cat : cats)
                    for (int i = 0; i < repeats; i++)
                        tr.read(cat);
                assertEquals(cats.size(), tr.runtimeIndexes().encounteredUniqueEvents.stream().count());
                if (rejected)
                {
                    ValidationStageExceptions failure =
                            assertThrows(ValidationStageExceptions.class, () -> tr.commit());
                    assertEquals(expectedViolations(), failure.multiple().length);
                    if (render)
                    {
                        long start = System.nanoTime();
                        consumed += failure.getMessage().length();
                        firstRendering += System.nanoTime() - start;
                        start = System.nanoTime();
                        consumed += failure.getMessage().length();
                        repeatedRendering += System.nanoTime() - start;
                    }
                    tr.rollback();
                }
                else
                    tr.commit();
                assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
            }
            finally
            {
                tr.close();
                cats.get(cats.size() - 1).color = "Black";
            }
            assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
        }

        public void close()
        {
            ex.close();
        }
    }
}
