package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.app.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * Runs complete transactions independently of selection-only dwelling measurements.
 * The fixture has many applicable type/event patterns but a fixed declaration count; exact
 * counters check deferred evaluation and repeated-event folding on every successful attempt.
 * Rejection explicitly rolls back, and recovery uses a fresh transaction after repairing data.
 *
 * Setup is supplied by the caller and stays outside timing. Warmups precede each measured
 * batch, allocated bytes come from the current thread, and every transaction closes in finally.
 * The helper depends only on stable APIs so baseline and candidate use identical workflow code.
 */
class DwellingWorkflowProbe
{
    void call(Existential ex, DwellingWorkload workload, int rules, AtomicLong calls) throws Exception
    {
        int repeats = Integer.getInteger("benchmark.reports", 1);
        measure("success", () -> success(ex, workload, repeats, rules, calls));
        measure("reject-rollback", () -> reject(ex));
        measure("recovery", () -> {
            reject(ex);
            success(ex, workload, repeats, rules, calls);
        });
    }

    void measure(String name, Workflow work) throws Exception
    {
        int warmup = Integer.getInteger("benchmark.warmup.iterations", 3);
        int iterations = Integer.getInteger("benchmark.measure.iterations", 5);
        for (int i = 0; i < warmup; i++)
            work.call();
        long bytes = BenchmarkMemory.allocated();
        long started = System.nanoTime();
        for (int i = 0; i < iterations; i++)
            work.call();
        long elapsed = System.nanoTime() - started;
        long allocated = BenchmarkMemory.allocated() - bytes;
        System.out.printf(Locale.ROOT, "dwelling workflow %s %.1f µs/workflow %.1f bytes/workflow%n",
                name, elapsed / 1_000d / iterations, (double) allocated / iterations);
    }

    void success(Existential ex, DwellingWorkload workload, int repeats, int rules, AtomicLong calls) throws Exception
    {
        long before = calls.get();
        Tr tr = ex.begin(DwellingSelectionProbe.OP);
        try
        {
            new DwellingOperations().report(tr, workload, repeats);
            if (calls.get() != before)
                throw new AssertionError("Validation ran during reporting");
            tr.commit();
            if (calls.get() - before != (long) workload.dwellings.size() * rules)
                throw new AssertionError("Incorrect folded callback count");
        }
        finally
        {
            tr.close();
        }
    }

    void reject(Existential ex) throws Exception
    {
        DwellingWorkload rejected = new DwellingWorkload(1);
        rejected.dwellings.get(0).color = null;
        Tr tr = ex.begin(DwellingSelectionProbe.OP);
        try
        {
            new DwellingOperations().report(tr, rejected, 1);
            try
            {
                tr.commit();
                throw new AssertionError("Expected the dwelling color rule to fail");
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

    interface Workflow
    {
        void call() throws Exception;
    }
}
