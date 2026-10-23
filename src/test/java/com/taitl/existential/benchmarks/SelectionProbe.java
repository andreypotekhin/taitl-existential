package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.evaluation.events.split_events.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * Compares cold and warm dispatch while switching only the library JAR on the classpath.
 * It uses the stable SplitEvent interface shared by the baseline and candidate, with identical
 * Night City configuration, preconstructed keys, and iteration counts in each fresh process.
 *
 * Setup and key construction precede measurement. Selection consumes handler counts without
 * executing callbacks; optional instrumentation measures the reachable field graph separately.
 * The enclosing library closes on every exit, including a failed count assertion.
 */
public class SelectionProbe
{
    static volatile long consumed;

    public static void main(String[] args) throws Exception
    {
        int types = Integer.getInteger("benchmark.types", 200);
        int rules = Integer.getInteger("benchmark.rules", 100);
        CityWorkload workload = new CityWorkload(types, types);
        AtomicLong callbacks = new AtomicLong();
        Existential ex = new Existential();
        Existential previous = Ex.instance(ex);
        EventField field = null;
        try
        {
            long started = System.nanoTime();
            String op = "/night-city/selection-probe";
            new ConfigureLooks().configureSelectionWorkload(op, workload, rules, "broad", callbacks);
            Tr tr = ex.begin(op);
            tr.close();
            field = ex.configs().config(op).indexes(op, StageName.VALIDATION).eventField();
            System.out.printf(Locale.ROOT, "probe setup %.1f µs configured=%d bytes%n",
                    (System.nanoTime() - started) / 1_000d, BenchmarkMemory.reachable(field));
            List<RuntimeKey<?>> keys = keys(workload);
            SplitEvent split = new SplitEvent();
            measure("cold", field, keys, split, 1, rules);
            for (int i = 0; i < 50; i++)
            {
                consumed = evaluate(field, keys, split);
            }
            measure("warm", field, keys, split, 500, rules);
            System.out.printf(Locale.ROOT, "probe warm-field=%d bytes callbacks=%d%n",
                    BenchmarkMemory.reachable(field), callbacks.get());
            if (callbacks.get() != 0)
            {
                throw new AssertionError("Selection must not execute callbacks");
            }
        }
        finally
        {
            ex.close();
            Ex.instance(previous);
        }
        System.out.printf(Locale.ROOT, "probe closed-field=%d bytes%n", BenchmarkMemory.reachable(field));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static List<RuntimeKey<?>> keys(CityWorkload workload)
    {
        List<RuntimeKey<?>> result = new ArrayList<>();
        for (CityWorkload.Report report : workload.looks)
        {
            result.add(new RuntimeKey(new Create(report.entity), report.type, report.entity));
        }
        return result;
    }

    static void measure(String phase, EventField field, List<RuntimeKey<?>> keys, SplitEvent split,
            int iterations, int rules) throws Exception
    {
        long before = BenchmarkMemory.allocated();
        long started = System.nanoTime();
        long checksum = 0;
        for (int i = 0; i < iterations; i++)
        {
            checksum += evaluate(field, keys, split);
        }
        long elapsed = System.nanoTime() - started;
        long allocated = BenchmarkMemory.allocated() - before;
        consumed = checksum;
        if (checksum != (long) keys.size() * (rules + 1) * iterations)
        {
            throw new AssertionError("Unexpected selected handler count: " + checksum);
        }
        System.out.printf(Locale.ROOT, "probe selection %s %.1f µs/selection %.1f bytes/selection checksum=%d%n",
                phase, elapsed / 1_000d / iterations / keys.size(),
                (double) allocated / iterations / keys.size(), checksum);
    }

    static long evaluate(EventField field, List<RuntimeKey<?>> keys, SplitEvent split) throws Exception
    {
        long count = 0;
        for (RuntimeKey<?> key : keys)
        {
            count += split.call(key, field, false, true, null).evaluables().size() + 1;
        }
        return count;
    }
}
