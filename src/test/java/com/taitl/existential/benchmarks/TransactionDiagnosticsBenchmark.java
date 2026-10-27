package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Shared baseline/candidate APIs; each error workflow includes explicit disposal and a fresh successful attempt. */
class TransactionDiagnosticsBenchmark
{
    static final String OP = "/city/transaction-diagnostics/benchmark";
    static volatile long consumed;

    @Test
    void workflows()
    {
        for (Scenario scenario : Scenario.values())
        {
            try (Fixture fixture = new Fixture(scenario, 1000))
            {
                measure(fixture, false);
                if (scenario != Scenario.SUCCESS)
                    measure(fixture, true);
            }
        }
    }

    void measure(Fixture fixture, boolean render)
    {
        int warmups = Integer.getInteger("benchmark.warmup.iterations", 3);
        int iterations = Integer.getInteger("benchmark.measure.iterations", 5);
        for (int i = 0; i < warmups; i++)
            fixture.run(render);
        long allocated = BenchmarkMemory.allocated();
        long started = System.nanoTime();
        for (int i = 0; i < iterations; i++)
            fixture.run(render);
        allocated = BenchmarkMemory.allocated() - allocated;
        System.out.printf(Locale.ROOT,
                "TRANSACTION-DIAGNOSTICS sample=%d scenario=%s attempts=%d render=%s ms=%.6f MB=%.6f checks=%d rollbacks=%d rejections=%d registry=0 cleanup=true%n",
                Integer.getInteger("benchmark.sample", 0), fixture.scenario, fixture.attempts, render,
                (System.nanoTime() - started) / 1_000_000d / iterations, allocated / 1_000_000d / iterations,
                fixture.expectedChecks(), fixture.scenario == Scenario.ROLLBACK_ONLY ? 1 : 0,
                fixture.scenario == Scenario.SUCCESS ? 0 : fixture.attempts);
    }

    enum Scenario
    {
        SUCCESS, CLOSED, ROLLBACK_ONLY, MISSING
    }

    static class Fixture implements AutoCloseable
    {
        Existential ex = new Existential();
        Cat cat = new Cat("Black", "Park");
        Scenario scenario;
        int attempts;
        long checks;
        long rollbacks;

        Fixture(Scenario scenario, int attempts)
        {
            long bytes = BenchmarkMemory.allocated();
            long started = System.nanoTime();
            this.scenario = scenario;
            this.attempts = attempts;
            ex.configure().context(OP).invariant(Cat.class).read(value -> {
                checks++;
                return "Black".equals(value.color);
            }, "Black cats pass");
            Tr setup = ex.begin(OP);
            setup.close();
            System.out.printf(Locale.ROOT, "STATE-SETUP sample=%d scenario=%s ms=%.6f MB=%.6f%n",
                    Integer.getInteger("benchmark.sample", 0), scenario,
                    (System.nanoTime() - started) / 1_000_000d, (BenchmarkMemory.allocated() - bytes) / 1_000_000d);
        }

        long expectedChecks()
        {
            return scenario == Scenario.SUCCESS ? attempts : scenario == Scenario.ROLLBACK_ONLY ? 2 : 1;
        }

        void run(boolean render)
        {
            checks = 0;
            rollbacks = 0;
            if (scenario == Scenario.SUCCESS)
            {
                for (int i = 0; i < attempts; i++)
                    success();
            }
            else if (scenario == Scenario.MISSING)
            {
                for (int i = 0; i < attempts; i++)
                {
                    String id = "missing-" + i;
                    reject(NotFoundException.class, () -> ex.transactions().tr(id), render);
                }
                success();
            }
            else
            {
                guarded(render);
                success();
            }
            assertEquals(expectedChecks(), checks);
            assertEquals(scenario == Scenario.ROLLBACK_ONLY ? 1 : 0, rollbacks);
            assertEquals(0, ex.transactions().logic().registry().size());
        }

        void guarded(boolean render)
        {
            Transaction local = new Transaction(OP, "Explicit cleanup");
            local.rollback(transaction -> rollbacks++);
            Tr tr = ex.begin(OP, local);
            try
            {
                if (scenario == Scenario.ROLLBACK_ONLY)
                {
                    cat.color = "Grey";
                    tr.read(cat);
                    assertThrows(ValidationStageExceptions.class, () -> tr.commit());
                    cat.color = "Black";
                }
                else
                    tr.close();
                for (int i = 0; i < attempts; i++)
                    reject(ExistentialException.class, () -> tr.read(cat), render);
                if (scenario == Scenario.ROLLBACK_ONLY)
                    tr.rollback();
            }
            finally
            {
                cat.color = "Black";
                tr.close();
            }
            assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
        }

        void reject(Class<? extends ExistentialException> type, Runnable request, boolean render)
        {
            ExistentialException failure = assertThrows(type, () -> request.run());
            assertEquals(type, failure.getClass());
            if (render)
            {
                consumed += failure.getMessage().length();
                consumed += failure.getMessage().length();
            }
        }

        void success()
        {
            Tr tr = ex.begin(OP);
            try
            {
                tr.read(cat);
                tr.commit();
                assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
            }
            finally
            {
                tr.close();
            }
        }

        public void close()
        {
            ex.close();
        }
    }
}
