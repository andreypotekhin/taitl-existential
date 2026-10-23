package com.taitl.ex.examples.night_city.app;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;
import java.lang.management.*;
import com.sun.management.ThreadMXBean;

public class NightCityExecutionBenchmark
{
    private static final String OP = "/night-city/cats/execution-benchmark";
    private static final String IMMEDIATE_OP = "/night-city/cats/execution-benchmark-immediate";
    private static final Logger LOGGER = Logger.getLogger(NightCityExecutionBenchmark.class.getName());
    private static final ThreadMXBean ALLOCATIONS = allocationBean();

    public static void main(String[] args) throws Exception
    {
        int size = Integer.getInteger("benchmark.entities", 10_000);
        int types = Integer.getInteger("benchmark.types", 100);
        int rules = Integer.getInteger("benchmark.rules", 100);
        boolean measureImmediate = Boolean.getBoolean("benchmark.immediate");
        int warmup = Integer.getInteger("benchmark.warmup.iterations", 3);
        int iterations = Integer.getInteger("benchmark.measure.iterations", 5);
        CityWorkload workload = new CityWorkload(size, types);
        AtomicLong evaluations = new AtomicLong();
        AtomicLong immediateEvaluations = new AtomicLong();
        AtomicLong catEvaluations = new AtomicLong();
        if (LOGGER.isLoggable(Level.INFO))
        {
            LOGGER.info(String.format(Locale.ROOT, "entities: %,d; descriptors: %,d; rules: %,d; dwellings: %,d",
                    workload.size(), workload.descriptors, rules, workload.dwellings.size()));
        }
        try (Existential ex = new Existential())
        {
            Existential previous = Ex.instance(ex);
            try
            {
                new ConfigureCats().configureWorkload(OP, workload.dwellings, rules, catEvaluations);
                new ConfigureLooks().configureWorkload(OP, rules, evaluations);
                if (measureImmediate)
                {
                    new ConfigureLooks().configureImmediateWorkload(IMMEDIATE_OP, rules, immediateEvaluations);
                }
                measure("success", warmup, iterations, () -> success(ex, workload, rules, evaluations));
                measurePhases("validation-phases", warmup, iterations,
                        () -> validationPhases(ex, workload, rules, evaluations));
                if (measureImmediate)
                {
                    measure("immediate-success", warmup, iterations,
                            () -> immediateSuccess(ex, workload, rules, immediateEvaluations));
                }
                measure("reject-rollback", warmup, iterations, () -> reject(ex));
                measure("recovery", warmup, iterations, () -> {
                    reject(ex);
                    success(ex, workload, rules, evaluations);
                });
                if (LOGGER.isLoggable(Level.INFO))
                {
                    LOGGER.info("configured validation callbacks: " + evaluations.get());
                }
            }
            finally
            {
                Ex.instance(previous);
            }
        }
    }

    private static void measurePhases(String name, int warmup, int iterations, MeasuredWorkflow workflow)
            throws Exception
    {
        for (int i = 0; i < warmup; i++)
        {
            workflow.run();
        }
        long report = 0;
        long commit = 0;
        long reportBytes = 0;
        long commitBytes = 0;
        for (int i = 0; i < iterations; i++)
        {
            long[] elapsed = workflow.run();
            report += elapsed[0];
            commit += elapsed[1];
            reportBytes += elapsed[2];
            commitBytes += elapsed[3];
        }
        if (LOGGER.isLoggable(Level.INFO))
        {
            LOGGER.info(String.format(Locale.ROOT,
                    "%s: %,d workflows, report=%,d ns/%,d bytes, commit=%,d ns/%,d bytes per workflow",
                    name, iterations, report / iterations, reportBytes / iterations, commit / iterations,
                    commitBytes / iterations));
        }
    }

    private static void measure(String name, int warmup, int iterations, Workflow workflow) throws Exception
    {
        for (int i = 0; i < warmup; i++)
        {
            workflow.run();
        }
        long elapsed = 0;
        long allocated = 0;
        for (int i = 0; i < iterations; i++)
        {
            long before = allocatedBytes();
            long started = System.nanoTime();
            workflow.run();
            elapsed += System.nanoTime() - started;
            long after = allocatedBytes();
            allocated += allocatedDelta(before, after);
        }
        if (LOGGER.isLoggable(Level.INFO))
        {
            LOGGER.info(String.format(Locale.ROOT,
                    "%s: %,d workflows, %,d ns/workflow, %,d allocated bytes/workflow",
                    name, iterations, elapsed / iterations, allocated / iterations));
        }
    }

    private static void success(Existential ex, CityWorkload workload, int rules, AtomicLong evaluations)
            throws Exception
    {
        Tr tr = ex.begin(OP);
        try
        {
            long before = evaluations.get();
            for (CityWorkload.Report report : workload.looks)
            {
                create(tr, report);
            }
            if (evaluations.get() != before)
            {
                throw new IllegalStateException("Validation rules ran while reporting events");
            }
            tr.commit();
            long expected = (long) workload.size() * rules;
            if (evaluations.get() - before != expected)
            {
                throw new IllegalStateException("Expected " + expected + " validation callbacks but observed "
                        + (evaluations.get() - before));
            }
        }
        finally
        {
            tr.close();
        }
    }

    @SuppressWarnings("PMD.PrematureDeclaration")
    private static long[] validationPhases(Existential ex, CityWorkload workload, int rules,
            AtomicLong evaluations) throws Exception
    {
        Tr tr = ex.begin(OP);
        try
        {
            long before = evaluations.get();
            long reportAllocated = allocatedBytes();
            long started = System.nanoTime();
            for (CityWorkload.Report report : workload.looks)
            {
                create(tr, report);
            }
            long reportTime = System.nanoTime() - started;
            long afterReportAllocated = allocatedBytes();
            if (evaluations.get() != before)
            {
                throw new IllegalStateException("Validation rules ran while reporting events");
            }
            long commitAllocated = allocatedBytes();
            started = System.nanoTime();
            tr.commit();
            long commitTime = System.nanoTime() - started;
            long afterCommitAllocated = allocatedBytes();
            long expected = (long) workload.size() * rules;
            if (evaluations.get() - before != expected)
            {
                throw new IllegalStateException("Expected " + expected + " validation callbacks but observed "
                        + (evaluations.get() - before));
            }
            return new long[] { reportTime, commitTime, allocatedDelta(reportAllocated, afterReportAllocated),
                    allocatedDelta(commitAllocated, afterCommitAllocated) };
        }
        finally
        {
            tr.close();
        }
    }

    private static void immediateSuccess(Existential ex, CityWorkload workload, int rules,
            AtomicLong evaluations) throws Exception
    {
        Tr tr = ex.begin(IMMEDIATE_OP);
        try
        {
            long before = evaluations.get();
            for (CityWorkload.Report report : workload.looks)
            {
                create(tr, report);
            }
            long expected = (long) workload.size() * rules;
            if (evaluations.get() - before != expected)
            {
                throw new IllegalStateException("Expected " + expected + " immediate callbacks but observed "
                        + (evaluations.get() - before));
            }
            tr.commit();
        }
        finally
        {
            tr.close();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void create(Tr tr, CityWorkload.Report report) throws ExistentialException
    {
        tr.create(report.entity, (com.taitl.existential.keys.TypeKey) report.type);
    }

    private static void reject(Existential ex) throws Exception
    {
        Tr tr = ex.begin(OP);
        try
        {
            tr.create(new Cat("Grey", "Park"));
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

    @FunctionalInterface
    private interface Workflow
    {
        void run() throws Exception;
    }

    private interface MeasuredWorkflow
    {
        long[] run() throws Exception;
    }

    private static ThreadMXBean allocationBean()
    {
        java.lang.management.ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        if (bean instanceof ThreadMXBean && ((ThreadMXBean) bean).isThreadAllocatedMemorySupported())
        {
            ThreadMXBean allocations = (ThreadMXBean) bean;
            if (!allocations.isThreadAllocatedMemoryEnabled())
            {
                allocations.setThreadAllocatedMemoryEnabled(true);
            }
            return allocations;
        }
        return null;
    }

    private static long allocatedBytes()
    {
        return ALLOCATIONS == null ? -1 : ALLOCATIONS.getThreadAllocatedBytes(Thread.currentThread().getId());
    }

    private static long allocatedDelta(long before, long after)
    {
        return before < 0 || after < before ? 0 : after - before;
    }
}
