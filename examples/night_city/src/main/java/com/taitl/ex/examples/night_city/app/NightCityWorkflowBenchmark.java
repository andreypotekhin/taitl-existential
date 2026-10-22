package com.taitl.ex.examples.night_city.app;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

import java.util.*;
import java.util.logging.*;

public final class NightCityWorkflowBenchmark
{
    private static final String OP = "/night-city/cats/benchmark";
    private static final Logger LOGGER = Logger.getLogger(NightCityWorkflowBenchmark.class.getName());

    private NightCityWorkflowBenchmark()
    {
    }

    public static void main(String[] args) throws Exception
    {
        for (int size : new int[] { 16, 256, 4096 })
        {
            try (Scenario scenario = new Scenario(size))
            {
                measure("success/" + size, () -> scenario.success());
                measure("reject-rollback/" + size, () -> scenario.rejectAndRollback());
                measure("recovery/" + size, () -> scenario.recover());
                measure("repeated16/" + size, () -> scenario.repeatedEvent());
            }
        }
    }

    private static void measure(String name, Workflow workflow) throws Exception
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
        long elapsed = System.nanoTime() - started;
        if (LOGGER.isLoggable(Level.INFO))
        {
            LOGGER.info(String.format(Locale.ROOT, "%-24s %,8d workflows %,12.1f ns/workflow",
                    name, iterations, (double) elapsed / iterations));
        }
    }

    @FunctionalInterface
    private interface Workflow
    {
        void run() throws Exception;
    }

    private static final class Scenario implements AutoCloseable
    {
        private final Existential ex = new Existential();
        private final Existential previous;
        private final Cat rejected = new Cat("Grey", "Park");
        private final Cat valid = new Cat("Black", "Park");

        Scenario(int size)
        {
            previous = Ex.instance(ex);
            Map<Cat, Collection<Dwelling<Cat, ?>>> dwellings = new LinkedHashMap<>();
            for (int i = 0; i < size; i++)
            {
                Cat source = new Cat("Black-" + i, "Place-" + i);
                dwellings.put(source, List.of(new Dwelling<Cat, Rugs>("Grey", source.location())));
            }
            dwellings.put(valid, List.of(new Dwelling<Cat, Rugs>("Grey", "Park")));
            dwellings.put(rejected, List.of(new Dwelling<Cat, Rugs>("Grey", "Park")));
            new ConfigureCats().configureLifecycle(OP, dwellings);
        }

        void success() throws Exception
        {
            Tr tr = ex.begin(OP);
            try
            {
                tr.create(valid);
                tr.commit();
            }
            finally
            {
                tr.close();
            }
        }

        void rejectAndRollback() throws Exception
        {
            Tr tr = ex.begin(OP);
            try
            {
                tr.create(rejected);
                try
                {
                    tr.commit();
                    throw new IllegalStateException("Expected the non-black cat rule to reject the operation");
                }
                catch (ValidationStageExceptions expected)
                {
                    tr.rollback();
                }
            }
            finally
            {
                tr.close();
            }
        }

        void recover() throws Exception
        {
            rejectAndRollback();
            success();
        }

        void repeatedEvent() throws Exception
        {
            Tr tr = ex.begin(OP);
            try
            {
                for (int i = 0; i < 16; i++)
                {
                    tr.read(valid);
                }
                tr.commit();
            }
            finally
            {
                tr.close();
            }
        }

        public void close()
        {
            try
            {
                ex.close();
            }
            finally
            {
                Ex.instance(previous);
            }
        }
    }
}
