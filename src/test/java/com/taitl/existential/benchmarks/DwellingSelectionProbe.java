package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.logic.configuration.indexes.*;
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
 * Compares actual library dispatch on the same many-pattern dwelling configuration.
 * Only the library JAR changes between runs; the probe uses interfaces present in the committed
 * array baseline. Setup, fresh-cache resolution, resident reuse, and whole workflows remain
 * separate, with exact selected/callback counts and optional reachable holder graphs.
 *
 * Fresh fields share finalized indexes but never a selection cache. Their construction and
 * disposal stay outside timing; cold measurement therefore includes first selection publication
 * with warmed class metadata. Every temporary field and the enclosing library close on failure.
 */
public class DwellingSelectionProbe
{
    static volatile long consumed;
    static final String OP = "/night-city/dwellings/probe";

    public static void main(String[] args) throws Exception
    {
        int rows = Integer.getInteger("benchmark.rows", 20);
        int rules = Integer.getInteger("benchmark.rules", 256);
        DwellingWorkload workload = new DwellingWorkload(Integer.getInteger("benchmark.entities", 10000));
        AtomicLong callbacks = new AtomicLong();
        Existential ex = new Existential();
        Existential previous = Ex.instance(ex);
        EventField field = null;
        try
        {
            long started = System.nanoTime();
            new ConfigureDwellings().configure(OP, rules, rows, false, declaration -> callbacks.incrementAndGet());
            Tr tr = ex.begin(OP);
            tr.close();
            ConfigurationIndexes indexes = ex.configs().config(OP).indexes(OP, StageName.VALIDATION);
            field = indexes.eventField();
            System.out.printf(Locale.ROOT, "dwelling setup rows=%d rules=%d %.1f µs configured=%d bytes%n",
                    rows, rules, (System.nanoTime() - started) / 1_000d, BenchmarkMemory.reachable(field));
            RuntimeKey<?> key = new RuntimeKey<>(new Create<>(workload.dwellings.get(0)), workload.type,
                    workload.dwellings.get(0));
            selections(indexes, key, rules);
            if (callbacks.get() != 0)
                throw new AssertionError("Selection executed a predicate");
            System.out.printf(Locale.ROOT, "dwelling warm-field=%d bytes%n", BenchmarkMemory.reachable(field));
            if (BenchmarkMemory.available())
            {
                for (Object value : BenchmarkMemory.graph(field))
                {
                    if (value instanceof DwellingWorkload || value instanceof Dwelling<?, ?>
                            || value instanceof RuntimeKey<?>)
                        throw new AssertionError("Shared cache retained a payload");
                }
            }
            new DwellingWorkflowProbe().call(ex, workload, rules, callbacks);
        }
        finally
        {
            ex.close();
            Ex.instance(previous);
        }
        System.out.printf(Locale.ROOT, "dwelling closed-field=%d bytes entries=%d%n",
                BenchmarkMemory.reachable(field), field.cachedSelections());
        if (field.cachedSelections() != 0)
            throw new AssertionError("Closed selections retained");
    }

    static void selections(ConfigurationIndexes indexes, RuntimeKey<?> key, int rules) throws Exception
    {
        SplitEvent split = new SplitEvent();
        long until = System.nanoTime() + 200_000_000;
        do
        {
            EventField field = new EventField(indexes);
            try
            {
                consumed = select(field, key, split);
            }
            finally
            {
                field.close();
            }
        }
        while (System.nanoTime() < until);
        List<EventField> fields = new ArrayList<>();
        for (int i = 0; i < 4096; i++)
            fields.add(new EventField(indexes));
        try
        {
            long bytes = BenchmarkMemory.allocated();
            long started = System.nanoTime();
            long count = 0;
            for (EventField field : fields)
                count += select(field, key, split);
            output("fresh", fields.size(), count, rules, started, bytes);
        }
        finally
        {
            for (EventField field : fields)
                field.close();
        }
        EventField field = indexes.eventField();
        for (int i = 0; i < 100000; i++)
            consumed = select(field, key, split);
        long bytes = BenchmarkMemory.allocated();
        long started = System.nanoTime();
        long count = 0;
        for (int i = 0; i < 1000000; i++)
            count += select(field, key, split);
        output("warm", 1000000, count, rules, started, bytes);
    }

    static long select(EventField field, RuntimeKey<?> key, SplitEvent split) throws Exception
    {
        return split.call(key, field, false, true, null).evaluables().size() + 1;
    }

    static void output(String phase, int iterations, long count, int rules, long started, long bytes)
    {
        long elapsed = System.nanoTime() - started;
        long allocated = BenchmarkMemory.allocated() - bytes;
        consumed = count;
        if (count != (long) iterations * (rules + 1))
            throw new AssertionError("Wrong selected count " + count);
        System.out.printf(Locale.ROOT, "dwelling selection %s %.1f µs/selection %.1f bytes/selection checksum=%d%n",
                phase, elapsed / 1_000d / iterations, (double) allocated / iterations, count);
    }
}
