package com.taitl.existential.benchmarks;

import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.transactions.*;
import com.taitl.ex.examples.night_city.model.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InstanceConfigurationBenchmark
{
    private static final String OP = "/bench/instance-configuration";

    @Test
    @DisplayName("Instance configuration and steady transaction benchmark")
    void benchmark() throws Exception
    {
        Result setup = measure("construct-configure-first-begin-close", () -> runSetupWorkflow());
        Result transaction = measureConfiguredTransactions();

        System.out.println();
        System.out.println("Instance configuration benchmark:");
        System.out.println(setup);
        System.out.println(transaction);
        assertTrue(setup.microsPerOperation() > 0);
        assertTrue(transaction.microsPerOperation() > 0);
    }

    private Result measure(String name, Workflow workflow) throws Exception
    {
        int warmup = Integer.getInteger("benchmark.warmup.iterations", 50);
        int iterations = Integer.getInteger("benchmark.measure.iterations", 250);
        for (int i = 0; i < warmup; i++)
        {
            workflow.run();
        }
        long started = System.nanoTime();
        for (int i = 0; i < iterations; i++)
        {
            workflow.run();
        }
        return new Result(name, iterations, System.nanoTime() - started);
    }

    private void runSetupWorkflow() throws Exception
    {
        try (Existential ex = new Existential())
        {
            ex.configure().context(OP).invariant(Cat.class).create(cat -> true, "accept benchmark cats");
            ex.begin(OP).commit();
        }
    }

    private Result measureConfiguredTransactions() throws Exception
    {
        try (Existential ex = new Existential())
        {
            ex.configure().context(OP).invariant(Cat.class).create(cat -> true, "accept benchmark cats");
            ex.begin(OP).commit();

            int warmup = Integer.getInteger("benchmark.warmup.iterations", 50) * 10;
            int iterations = Integer.getInteger("benchmark.measure.iterations", 250) * 10;
            for (int i = 0; i < warmup; i++)
            {
                ex.begin(OP).commit();
            }
            long started = System.nanoTime();
            for (int i = 0; i < iterations; i++)
            {
                ex.begin(OP).commit();
            }
            return new Result("configured-empty-transaction", iterations, System.nanoTime() - started);
        }
    }

    @FunctionalInterface
    private interface Workflow
    {
        void run() throws Exception;
    }

    private static final class Result
    {
        private final String name;
        private final int iterations;
        private final long elapsedNanos;

        Result(String name, int iterations, long elapsedNanos)
        {
            this.name = name;
            this.iterations = iterations;
            this.elapsedNanos = elapsedNanos;
        }

        double microsPerOperation()
        {
            return elapsedNanos / 1_000d / iterations;
        }

        public String toString()
        {
            return String.format(Locale.ROOT, "  %-40s %,12.1f µs/op", name, microsPerOperation());
        }
    }
}
