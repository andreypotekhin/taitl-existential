package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomTransactionBenchmark
{
    protected static final String OP = "/bench/cats/update";
    protected static final Cat CAT = new Cat("Black", "Garden");

    @Test
    @DisplayName("Custom transaction benchmark")
    void customTransactionBenchmark() throws Exception
    {
        BenchmarkResult staticContext = measure("static-context", StaticContextScenario::new);
        BenchmarkResult configuredTransaction = measure("configured-transaction", ConfiguredTransactionScenario::new);
        BenchmarkResult customTransaction = measure("custom-transaction", CustomTransactionScenario::new);

        System.out.println();
        System.out.println("Custom transaction benchmark:");
        System.out.println(staticContext);
        System.out.println(configuredTransaction);
        System.out.println(customTransaction);

        assertTrue(staticContext.opsPerSecond() > 0);
        assertTrue(configuredTransaction.opsPerSecond() > 0);
        assertTrue(customTransaction.opsPerSecond() > 0);
    }

    protected BenchmarkResult measure(String name, ScenarioFactory factory) throws Exception
    {
        sane(factory, "factory");
        try (Scenario scenario = factory.create())
        {
            scenario.run(warmupIterations());
            long started = System.nanoTime();
            scenario.run(measureIterations());
            long elapsed = System.nanoTime() - started;
            return new BenchmarkResult(name, measureIterations(), elapsed);
        }
    }

    protected int warmupIterations()
    {
        return Integer.getInteger("benchmark.warmup.iterations", 2000);
    }

    protected int measureIterations()
    {
        return Integer.getInteger("benchmark.measure.iterations", 10000);
    }

    protected interface ScenarioFactory
    {
        Scenario create() throws Exception;
    }

    protected interface Scenario extends AutoCloseable
    {
        void run(int iterations) throws Exception;

        default void close() throws Exception
        {
        }
    }

    protected static class BenchmarkResult
    {
        protected final String name;
        protected final int iterations;
        protected final long elapsedNanos;

        BenchmarkResult(String name, int iterations, long elapsedNanos)
        {
            this.name = name;
            this.iterations = iterations;
            this.elapsedNanos = elapsedNanos;
        }

        double nanosPerOperation()
        {
            return (double) elapsedNanos / iterations;
        }

        long opsPerSecond()
        {
            return Math.round(1_000_000_000d / nanosPerOperation());
        }

        public String toString()
        {
            return String.format(Locale.ROOT,
                    "  %-24s %,10d ops/s  %,12.1f ns/op",
                    name,
                    opsPerSecond(),
                    nanosPerOperation());
        }
    }

    protected abstract static class BenchmarkScenario implements Scenario
    {
        protected final Existential ex;
        protected final AtomicInteger updates;

        BenchmarkScenario()
        {
            this.ex = new Existential();
            this.updates = new AtomicInteger();
            configure();
        }

        public void run(int iterations) throws Exception
        {
            for (int i = 0; i < iterations; i++)
            {
                Tr tr = begin();
                tr.update(CAT);
                tr.commit();
            }
        }

        public void close()
        {
            ex.close();
        }

        protected abstract void configure();

        protected abstract Tr begin() throws Exception;
    }

    protected static class StaticContextScenario extends BenchmarkScenario
    {
        protected void configure()
        {
            // @formatter:off
            ex.configure()
                .context(OP)
                    .effect(Cat.class)
                        .update(cat -> updates.incrementAndGet())
                    ;
            // @formatter:on
        }

        protected Tr begin() throws Exception
        {
            return ex.begin(OP);
        }
    }

    protected static class ConfiguredTransactionScenario extends BenchmarkScenario
    {
        protected void configure()
        {
            // @formatter:off
            ex.configure()
                .context(OP)
                    .transaction(() -> {
                        Transaction transaction = new Transaction(OP, "configured-benchmark");
                        Effect<Cat> effect = new Effect<>(Cat.class);
                        effect.update(cat -> updates.incrementAndGet());
                        transaction.effect(effect);
                        return transaction;
                    })
                    ;
            // @formatter:on
        }

        protected Tr begin() throws Exception
        {
            return ex.begin(OP);
        }
    }

    protected static class CustomTransactionScenario extends BenchmarkScenario
    {
        protected void configure()
        {
            // @formatter:off
            ex.configure()
                .context(OP)
                    .transaction(() -> new Transaction(OP, "base-benchmark"))
                    ;
            // @formatter:on
        }

        protected Tr begin() throws Exception
        {
            Transaction transaction = new Transaction(OP, "custom-benchmark");
            Effect<Cat> effect = new Effect<>(Cat.class);
            effect.update(cat -> updates.incrementAndGet());
            transaction.effect(effect);
            return ex.begin(OP, transaction);
        }
    }

    protected static void sane(Object value, String label)
    {
        assertNotNull(value, label + " should not be null");
    }
}
