package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;

class AccessEventBenchmark
{
    static final String OP = "/city/access/benchmark";

    @Test
    void measuresCompleteTypedAndGeneralAccessWorkflows()
    {
        for (int repeats : new int[] { 1, 8 })
        {
            for (boolean common : Boolean.getBoolean("benchmark.baseline")
                    ? new boolean[] { false }
                    : new boolean[] { false, true })
            {
                try (Fixture fixture = new Fixture(10000, repeats, common))
                {
                    fixture.run(false);
                    measure(fixture, "success", () -> fixture.run(false));
                    measure(fixture, "rejection-rollback", () -> fixture.run(true));
                    measure(fixture, "fresh-recovery", () -> {
                        fixture.run(true);
                        fixture.run(false);
                    });
                }
            }
        }
    }

    void measure(Fixture fixture, String scenario, Runnable workflow)
    {
        long bytes = BenchmarkMemory.allocated();
        long started = System.nanoTime();
        workflow.run();
        double elapsed = (System.nanoTime() - started) / 1_000_000d;
        long allocated = BenchmarkMemory.allocated() - bytes;
        System.out.printf(Locale.ROOT,
                "sample=%d entities=%d repeats=%d common=%s scenario=%s ms=%.1f allocated-MB=%.1f%n",
                Integer.getInteger("benchmark.sample", 0), fixture.cats.size(), fixture.repeats,
                fixture.common, scenario, elapsed, allocated / 1_000_000d);
    }

    static class Fixture implements AutoCloseable
    {
        Existential ex = new Existential();
        List<Cat> cats = new ArrayList<>();
        TypeKey<Cat> type = new TypeKey<>(Cat.class);
        AtomicLong checks = new AtomicLong();
        AtomicLong intents = new AtomicLong();
        int repeats;
        boolean common;

        Fixture(int count, int repeats, boolean common)
        {
            this.repeats = repeats;
            this.common = common;
            for (int i = 0; i < count; i++)
            {
                cats.add(new Cat("Black", "Park"));
            }
            // @formatter:off
            ex.configure().context(OP).invariant(Cat.class)
                    .all(cat -> {
                        checks.incrementAndGet();
                        return "Black".equals(cat.color);
                    }, "Black cats pass")
                    .read(cat -> checks.incrementAndGet() > 0)
                    .rl(cat -> checks.incrementAndGet() > 0)
                    .write(cat -> checks.incrementAndGet() > 0);
            ex.configure().context(OP).intent(Cat.class)
                    .read(cat -> intents.incrementAndGet() > 0)
                    .rl(cat -> intents.incrementAndGet() > 0)
                    .write(cat -> intents.incrementAndGet() > 0);
            if (common)
            {
                ex.configure().context(OP)
                        .invariant(new Invariant<>(Cat.class).on(cat -> checks.incrementAndGet() > 0))
                        .intent(new Intent<>(Cat.class).on(cat -> intents.incrementAndGet() > 0), type);
            }
            // @formatter:on
            Tr setup = ex.begin(OP);
            setup.close();
        }

        void run(boolean rejected)
        {
            long beforeChecks = checks.get();
            long beforeIntents = intents.get();
            if (rejected)
            {
                cats.get(0).color = "Grey";
            }
            Tr tr = ex.begin(OP);
            try
            {
                for (Cat cat : cats)
                {
                    for (int i = 0; i < repeats; i++)
                    {
                        tr.read(cat);
                        tr.event(new RL<>(cat), cat, type);
                        tr.write(cat);
                        tr.event(new Access<>(cat), cat, type);
                    }
                }
                if (rejected)
                {
                    assertThrows(ValidationStageExceptions.class, () -> tr.commit());
                    tr.rollback();
                }
                else
                {
                    tr.commit();
                }
                assertEquals((common ? 12L : 8L) * cats.size(), checks.get() - beforeChecks);
                assertEquals((common ? 8L : 4L) * cats.size() * repeats, intents.get() - beforeIntents);
                assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
            }
            finally
            {
                cats.get(0).color = "Black";
                tr.close();
            }
        }

        public void close()
        {
            ex.close();
        }
    }
}
